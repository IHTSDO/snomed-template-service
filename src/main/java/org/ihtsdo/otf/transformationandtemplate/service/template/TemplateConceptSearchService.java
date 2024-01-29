package org.ihtsdo.otf.transformationandtemplate.service.template;

import org.ihtsdo.otf.rest.client.RestClientException;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowstormRestClient;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowstormRestClientFactory;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.AxiomPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RelationshipPojo;
import org.ihtsdo.otf.rest.exception.ResourceNotFoundException;
import org.ihtsdo.otf.transformationandtemplate.service.ConstantStrings;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.logical.Attribute;
import org.snomed.authoringtemplate.domain.logical.AttributeGroup;
import org.snomed.authoringtemplate.domain.logical.LogicalTemplate;
import org.snomed.authoringtemplate.service.LogicalTemplateParserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class TemplateConceptSearchService {

	private static final String AND = "AND";

	private static final String OR = "OR";

	private static final String CARDINALITY_SEPARATOR = "..";

	@Autowired 
	private LogicalTemplateParserService logicalTemplateParser;

	@Autowired
	private TemplateConceptTransformService templateConceptTransformService;

	@Autowired
	private SnowstormRestClientFactory terminologyClientFactory;
	
	@Autowired
	private TemplateService templateService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateConceptSearchService.class);

	private static final int MAX = 200000;

	@Value("${transformation.batch.max}")
	private int batchMax;

	private final ExecutorService executorService = Executors.newFixedThreadPool(10);

	public Set<String> searchConceptsByTemplate(String templateName, String branchPath, 
			Boolean logicalMatch, Boolean lexicalMatch, boolean stated) throws ServiceException, ResourceNotFoundException {
		
			LOGGER.info("Search concepts for template={}, on branchPath={}, with logicalMatch={}, lexicalMatch={} and stated={}",
					templateName, branchPath, logicalMatch, lexicalMatch, stated);
			if (logicalMatch == null) {
				throw new IllegalArgumentException("logicalMatch parameter must be specified.");
			}
			
			if (lexicalMatch != null) {
				if (!logicalMatch) {
					throw new IllegalArgumentException("logicalMatch parameter must be true when lexicalMatch is set.");
				}
			}
			try {
				ConceptTemplate conceptTemplate = templateService.loadOrThrow(templateName);
				// Parse logical
				LogicalTemplate logical = logicalTemplateParser.parseTemplate(conceptTemplate.getLogicalTemplate());
				if (lexicalMatch != null) {
					Set<String> logicalResult = performLogicalSearch(conceptTemplate, logical, branchPath, true, stated);
					return performLexicalSearch(conceptTemplate, logicalResult, branchPath, logical, lexicalMatch);
				} else {
					return performLogicalSearch(conceptTemplate, logical, branchPath, logicalMatch, stated);
				}
		} catch (IOException e) {
			throw new ServiceException("Failed to load template " + templateName);
		}
	}
	
	private Set<String> performLexicalSearch(ConceptTemplate conceptTemplate,
			Set<String> logicalMatched, String branchPath, LogicalTemplate logical, boolean lexicalMatch) throws ServiceException {
		
		Set<String> result = new HashSet<>();
		if (logicalMatched == null || logicalMatched.isEmpty()) {
			LOGGER.info("No results found for logical search.");
			return result;
		}

		try {
			Collection<ConceptPojo> concepts = terminologyClientFactory.getClient()
					.searchConcepts(branchPath, new ArrayList<>(logicalMatched));

			Map<String, List<DescriptionPojo>> originalConceptToDescriptionMap = new HashMap<>();
			for (ConceptPojo conceptPojo : concepts) {
				List<DescriptionPojo> originalDescriptions =  new ArrayList<>();
				conceptPojo.getDescriptions().forEach(desc -> {
					DescriptionPojo clonedDescription = new DescriptionPojo();
					BeanUtils.copyProperties(desc, clonedDescription);
					originalDescriptions.add(clonedDescription);
				});
				originalConceptToDescriptionMap.put(conceptPojo.getConceptId(), originalDescriptions);
			}

			TemplateTransformRequest transformRequest = new TemplateTransformRequest();
			transformRequest.setDestinationTemplate(conceptTemplate.getName());
			TemplateTransformation transformation = templateConceptTransformService.createTemplateTransformation(branchPath, transformRequest);

			// Start transformations in multiple threads
			List<Future<List<ConceptPojo>>> futureTasks = performTransform(concepts, transformation, logical, terminologyClientFactory.getClient());

			// Gather transformed concepts
			try {
				List<ConceptPojo> transformationConcepts = new ArrayList<>();
				for (Future<List<ConceptPojo>> future : futureTasks) {
					transformationConcepts.addAll(future.get());
				}
				transformationConcepts.forEach(transformationResult -> {
					transformationConcepts.forEach(conceptPojo -> {
						int count = 0;
						for (DescriptionPojo transformedDesc : conceptPojo.getDescriptions()) {
							for (DescriptionPojo originalDesc : originalConceptToDescriptionMap.get(conceptPojo.getConceptId())) {
								if (transformedDesc.getTerm().equals(originalDesc.getTerm()) && originalDesc.isActive() && transformedDesc.getType().equals(originalDesc.getType())) {
									count++;
									break;
								}
							}
						}
						boolean isMatched = count > 0 && count == conceptPojo.getDescriptions().size();
						if (lexicalMatch && isMatched) {
							result.add(conceptPojo.getConceptId());
						} else if (!lexicalMatch && !isMatched) {
							result.add(conceptPojo.getConceptId());
						}
					});
				});
				LOGGER.info("Logical search results={} and lexical search results={}", logicalMatched.size(), result.size());
				return result;
			} catch (ExecutionException | InterruptedException e) {
				throw new ServiceException("Failed to complete lexical template search.", e);
			}
        } catch (RestClientException e) {
			throw new ServiceException("Failed to complete lexical template search.", e);
		}
    }

	public List<Future<List<ConceptPojo>>> performTransform(Collection<ConceptPojo> concepts, TemplateTransformation transformation, LogicalTemplate logical, SnowstormRestClient restClient) throws ServiceException {
		String branchPath = transformation.getBranchPath();
		TemplateTransformRequest transformRequest = transformation.getTransformRequest();
		String destinationTemplate = transformRequest.getDestinationTemplate();

		List<Future<List<ConceptPojo>>> results = new ArrayList<>();
		try {
			ConceptTemplate destination = templateService.loadOrThrow(destinationTemplate);
			TransformationInputData input = new TransformationInputData(transformRequest);
			input.setDestinationSlotToAttributeMap(TemplateUtil.getSlotToAttributeMap(logical, true));
			input.setDestinationTemplate(destination);

			input.setBranchPath(branchPath);
			input.setConceptIdMap(templateConceptTransformService.getDestinationConceptsMap(branchPath, restClient, destination));
			List<ConceptPojo> batchJob = null;
			int counter=0;
			for (ConceptPojo conceptPojo : concepts) {
				if (batchJob == null) {
					batchJob = new ArrayList<>();
				}
				batchJob.add(conceptPojo);
				counter++;
				if (counter % batchMax == 0 || counter == transformRequest.getConceptsToTransform().size()) {
					// Do work
					final List<ConceptPojo> task = batchJob;
					results.add(executorService.submit(() -> batchTransform(input, task, restClient)));
					batchJob = null;
				}
			}
		} catch (IOException e) {
			throw new ServiceException("Failed to load template " + destinationTemplate, e);
		}
		return results;
	}

	public List<ConceptPojo> batchTransform(TransformationInputData input, List<ConceptPojo> conceptPojos, SnowstormRestClient restClient) throws ServiceException {
		List<ConceptPojo> transformedConcepts = new ArrayList<>();
		if (conceptPojos != null) {
			for (ConceptPojo pojo : conceptPojos) {
				transformedConcepts.add(templateConceptTransformService.performTransform(pojo, input, restClient));
			}
		}
		return transformedConcepts;
	}

	private Set<String> performLogicalSearch(ConceptTemplate conceptTemplate, LogicalTemplate logical,
			String branchPath, boolean logicalMatch, boolean stated) throws ServiceException {
		try {
			List<String> focusConcepts = logical.getFocusConcepts();
			String domainEcl = constructEclQuery(focusConcepts, Collections.emptyList(), Collections.emptyList());
			LOGGER.debug("Domain ECL=" + domainEcl);
			List<AttributeGroup> attributeGroups = logical.getAttributeGroups();
			List<Attribute> unGroupedAttributes = logical.getUngroupedAttributes();
			String logicalEcl = constructEclQuery(focusConcepts, attributeGroups, unGroupedAttributes);
			LOGGER.debug("Logic template ECL=" + logicalEcl);
			String ecl = constructLogicalSearchEcl(domainEcl, logicalEcl, logicalMatch);
			LOGGER.info("Logical search ECL={} stated={}", ecl, stated);
			Set<String> results = new HashSet<>(terminologyClientFactory.getClient().eclQuery(branchPath, ecl, MAX, stated));
			List<ConceptPojo> conceptPojos = terminologyClientFactory.getClient().searchConcepts(branchPath, new ArrayList<>(results));
			Set<String> toRemove = findConceptsNotMatchExactly(conceptPojos, attributeGroups, unGroupedAttributes, stated);
			if (!toRemove.isEmpty()) {
				LOGGER.info("Total concepts " + toRemove.size() + " are removed from results.");
				results.removeAll(toRemove);
			}
			LOGGER.info("Logical results {}", results.size());
			return results;
		} catch (Exception e) {
			String msg = "Failed to complete logical template search for template " + conceptTemplate.getName();
			if (e.getCause() != null && e.getCause().getMessage() != null) {
				msg = msg + " due to " +  e.getCause().getMessage();
			}
			throw new ServiceException(msg, e);
		}
	}

	protected Set<String> findConceptsNotMatchExactly(List<ConceptPojo> conceptPojos, List<AttributeGroup> attributeGroups,
			List<Attribute> unGroupedAttributes, boolean stated) {
		
		List<Set<String>> allTypes = new ArrayList<>();
		List<Set<String>> mandatoryTypes = new ArrayList<>();
		for (AttributeGroup group : attributeGroups) {
			Set<String> groupTypes = new HashSet<>();
			Set<String> mandatoryGroupTypes = new HashSet<>();
			for (Attribute attribute : group.getAttributes()) {
				groupTypes.add(attribute.getType());
				if ("1".equals(attribute.getCardinalityMin())) {
					mandatoryGroupTypes.add(attribute.getType());
				}
			}
			allTypes.add(groupTypes);
			if ("1".equals(group.getCardinalityMin())) {
				mandatoryTypes.add(mandatoryGroupTypes);
			}
		}

		Set<String> ungrouped = new HashSet<>();
		Set<String> mandatoryUngrouped = new HashSet<>();
		ungrouped.add(ConstantStrings.IS_A);
		mandatoryUngrouped.add(ConstantStrings.IS_A);
		for (Attribute attr : unGroupedAttributes) {
			ungrouped.add(attr.getType());
			if ("1".equals(attr.getCardinalityMin())) {
				mandatoryUngrouped.add(attr.getType());
			}
		}
		allTypes.add(ungrouped);
		mandatoryTypes.add(mandatoryUngrouped);
		
		Set<String> missing = new HashSet<>();
		Set<String> havingExtra = new HashSet<>();
		for (ConceptPojo concept : conceptPojos) {
			// Map relationship by group
			List<RelationshipPojo> activeRelationships = new ArrayList<>();
			if (stated) {
				if (concept.getClassAxioms() != null) {
					for (AxiomPojo axiom: concept.getClassAxioms()) {
						if (axiom.isActive()) {
							activeRelationships.addAll(axiom.getRelationships());
						}
					}
				} 
			} else {
				activeRelationships = concept.getRelationships().stream()
						.filter(RelationshipPojo::isActive)
						.filter(r -> r.getCharacteristicType().equals(ConstantStrings.INFERRED))
						.collect(Collectors.toList());
			}
			
			Map<Integer, Set<String>> relGroupMap = new HashMap<>();
			for (RelationshipPojo pojo : activeRelationships) {
				relGroupMap.computeIfAbsent(pojo.getGroupId(), k -> new HashSet<>())
				.add(pojo.getType().getConceptId());
			}
			
			if (missingMandatoryAttribute(mandatoryTypes, relGroupMap.values())) {
				missing.add(concept.getConceptId());
			}
			
			if (containExtraAttribute(allTypes, relGroupMap.values())) {
				havingExtra.add(concept.getConceptId());
			}
		}
		
		if (!missing.isEmpty()) {
			LOGGER.info("Total concepts missing at least one mandatory attribute:" + missing.size());
			LOGGER.debug("Concept ids " +  missing);
		}
		if (!havingExtra.isEmpty()) {
			LOGGER.info("Total concepts containing extra attribute:" + havingExtra.size());
			LOGGER.debug("Concept ids " +  havingExtra);
		}
		Set<String> results = new HashSet<>();
		results.addAll(missing);
		results.addAll(havingExtra);
		return results;
	}
	
	private boolean missingMandatoryAttribute(List<Set<String>> mandatoryTypes, Collection<Set<String>> relGroups) {
		// Check all mandatory attributes are present
		for (Set<String> mandatory : mandatoryTypes) {
			boolean isFound = false;
			for (Set<String> typeSet : relGroups) {
				if (typeSet.containsAll(mandatory)) {
					isFound = true;
					break;
				}
			}
			if (!isFound) {
				return true;
			}
		}
		return false;
	}

	private boolean containExtraAttribute(List<Set<String>> allTypes, Collection<Set<String>> relGroups) {
		// Check no additional types
		for (Set<String> typeSet : relGroups) {
			boolean isFound = false;
			for (Set<String> allType : allTypes) {
				if (allType.containsAll(typeSet)) {
					isFound = true;
					break;
				}
			}
			if (!isFound) {
				return true;
			}
		}
		return false;
	}

	private String constructLogicalSearchEcl(String domainEcl, String logicalEcl, boolean logicalMatch) {
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		builder.append(domainEcl);
		builder.append(")");
		if (logicalMatch) {
			builder.append(" AND ");
		} else {
			builder.append(" MINUS ");
		}
		builder.append("(");
		builder.append(logicalEcl);
		builder.append(")");
		return builder.toString();
	}

	private Map<String,String> mapSlots(List<Attribute> attributes) {
		Map<String, String> slotMap = new HashMap<>();
		for (Attribute attribute : attributes) {
			if (attribute.getValueSlotName() != null) {
				String slotValue = null;
				if (attribute.getValue() != null) {
					slotValue = attribute.getValue();
				} else if (attribute.getValueAllowableRangeECL() != null) {
					slotValue = attribute.getValueAllowableRangeECL();
				}
				slotMap.put(attribute.getValueSlotName(), slotValue);
			}
		}
		return slotMap;
	}
	
	private String replaceSlot(String ecl, List<Attribute> attributes) {
		Map<String, String> slotMap = mapSlots(attributes);
		String result = ecl;
		for (String name : slotMap.keySet()) {
			result = result.replaceAll("=" + name, "=" + slotMap.get(name));
		}
		return result;
	}

	private String convertAttributeToEcl(List<Attribute> attributes) {
		StringBuilder queryBuilder = new StringBuilder();
		int counter = 0;
		for (Attribute attribute : attributes) {
			if (counter++ > 0) {
				queryBuilder.append(",");
			}
			if (attribute.getCardinalityMin() != null) {
				queryBuilder.append("[").append(attribute.getCardinalityMin()).append(CARDINALITY_SEPARATOR);
			}
			if (attribute.getCardinalityMax() != null) {
				queryBuilder.append(attribute.getCardinalityMax()).append("]");
			}
			queryBuilder.append(attribute.getType());
			queryBuilder.append( "=");
			if (attribute.getValue() != null) {
				queryBuilder.append(attribute.getValue());
			}
			else if (attribute.getValueAllowableRangeECL() != null) {
				boolean isCompound = false;
				if (attribute.getValueAllowableRangeECL().contains(OR) || attribute.getValueAllowableRangeECL().contains(AND)) {
					isCompound = true;
					queryBuilder.append("(");
				}
				queryBuilder.append(attribute.getValueAllowableRangeECL());
				if (isCompound) {
					queryBuilder.append(")");
				}
			} else if (attribute.getValueSlotReference() != null) {
				queryBuilder.append(attribute.getValueSlotReference());
			}
		}
		return queryBuilder.toString();
	}

	public String constructEclQuery(List<String> focusConcepts, List<AttributeGroup> attributeGroups,
			List<Attribute> unGroupedAttriburtes) throws ServiceException {

		List<Attribute> attributes = new ArrayList<>(unGroupedAttriburtes);
		StringBuilder queryBuilder = new StringBuilder();
		if (focusConcepts == null || focusConcepts.isEmpty()) {
			throw new ServiceException("No focus concepts defined!");
		}
		queryBuilder.append("<<").append(focusConcepts.get(0));
		if (!attributeGroups.isEmpty() || !unGroupedAttriburtes.isEmpty()) {
			queryBuilder.append(":");
		}
		queryBuilder.append(convertAttributeToEcl(unGroupedAttriburtes));
		if (!unGroupedAttriburtes.isEmpty() && !attributeGroups.isEmpty()) {
			queryBuilder.append(",");
		}
		int groupCounter = 0;
		for (AttributeGroup group : attributeGroups) {
			attributes.addAll(group.getAttributes());
			if (groupCounter++ > 0) {
				queryBuilder.append(",");
			}
			if (group.getCardinalityMin() != null) {
				queryBuilder.append("[").append(group.getCardinalityMin()).append(CARDINALITY_SEPARATOR);
			}
			if (group.getCardinalityMax() != null) {
				queryBuilder.append(group.getCardinalityMax()).append("]");
			}
			queryBuilder.append("{");
			queryBuilder.append(convertAttributeToEcl(group.getAttributes()));
			queryBuilder.append("}");
		}
		 return replaceSlot(queryBuilder.toString(), attributes);
	}
}
