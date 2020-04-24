package org.ihtsdo.otf.transformationandtemplate.transform.service;

import org.ihtsdo.otf.transformationandtemplate.service.TemplateService;
import org.ihtsdo.otf.transformationandtemplate.service.TemplateUtil;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.transformationandtemplate.transform.*;
import org.ihtsdo.otf.rest.client.RestClientException;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowOwlRestClient;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptMiniPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.DefinitionStatus;
import org.snomed.authoringtemplate.domain.Relationship;
import org.snomed.authoringtemplate.domain.logical.Attribute;
import org.snomed.authoringtemplate.domain.logical.LogicalTemplate;
import org.snomed.authoringtemplate.service.LogicalTemplateParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class TemplateConceptTransformService {

	@Autowired
	private TemplateService templateService;
	
	@Autowired
	private TemplateTransformationResultService resultService;
	
	private final ExecutorService executorService = Executors.newFixedThreadPool(10);

	private final LogicalTemplateParserService parser;

	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateConceptTransformService.class);

	@Value("${transformation.batch.max}")
	private int batchMax;

	public TemplateConceptTransformService() {
		parser = new LogicalTemplateParserService();
	}

	@Async
	public void transformAsynchronously(TemplateTransformation transformation, SnowOwlRestClient restClient) throws ServiceException {
		transformation.setStatus(TransformationStatus.RUNNING);
		resultService.update(transformation);

		try {
			// Start transformations in multiple threads
			List<Future<TransformationResult>> futureTasks = transform(transformation, restClient);

			// Gather transformed concepts and any errors from the transformation jobs
			try {
				List<TransformationResult> transformationResults = new ArrayList<>();
				for (Future<TransformationResult> future : futureTasks) {
					transformationResults.add(future.get());
				}

				TransformationResult finalResult = new TransformationResult();

				finalResult.setConcepts(transformationResults.stream()
						.map(TransformationResult::getConcepts).flatMap(Collection::stream).collect(Collectors.toList()));

				finalResult.setFailures(transformationResults.stream().map(transformationResult -> transformationResult.getFailures().entrySet())
						.flatMap(Collection::stream).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

				transformation.setStatus(finalResult.getFailures().isEmpty() ? TransformationStatus.COMPLETED : TransformationStatus.COMPLETED_WITH_FAILURE);
				resultService.writeResultsToFile(transformation, finalResult);

			} catch (InterruptedException | ExecutionException e) {
				String errorMsg = "Unexpected errors while merging transformation results.";
				LOGGER.error(errorMsg, e);
				transformation.setStatus(TransformationStatus.FAILED);
				transformation.setErrorMsg(errorMsg + getErrorMsg(e));
			}
		} catch (ServiceException e) {
			LOGGER.error("Transformation failed.", e);
			transformation.setStatus(TransformationStatus.FAILED);
			transformation.setErrorMsg(e.getMessage());
		} finally {
			resultService.update(transformation);
		}
	}
	
	private String getErrorMsg(Throwable t) {
		if (t.getMessage() != null) {
			return t.getMessage();
		} else {
			return "Unexpected error caused by " + t.getCause().getMessage();
		}
	}
	
	private TransformationInputData constructTransformationInputData(ConceptTemplate destination, TemplateTransformRequest transformRequest) throws ServiceException {
		try {
			TransformationInputData input = new TransformationInputData(transformRequest);
			LogicalTemplate logical = parser.parseTemplate(destination.getLogicalTemplate());
			input.setDestinationSlotToAttributeMap(TemplateUtil.getSlotToAttributeMap(logical, true));
			input.setDestinationTemplate(destination);
			return input;
		} catch (IOException e) {
			throw new ServiceException("Failed to parse logical template " + destination.getName(), e);
		}
	}

	public List<Future<TransformationResult>> transform(TemplateTransformation transformation, SnowOwlRestClient restClient) throws ServiceException {
		
		String branchPath = transformation.getBranchPath();
		TemplateTransformRequest transformRequest = transformation.getTransformRequest();
		String destinationTemplate = transformRequest.getDestinationTemplate();
		
		List<Future<TransformationResult>> results = new ArrayList<>();
		ConceptTemplate source = null;
		ConceptTemplate destination;
		try {
			source = templateService.loadOrThrow(transformRequest.getSourceTemplate());
			destination = templateService.loadOrThrow(destinationTemplate);
			validate(source, destination);
			Map<String, ConceptMiniPojo> conceptMap = getDestinationConceptsMap(branchPath, restClient, destination);
			final TransformationInputData input = constructTransformationInputData(destination, transformRequest);
			input.setBranchPath(branchPath);
			input.setConceptIdMap(conceptMap);
			List<String> batchJob = null;
			int counter=0;
			for (String conceptId : transformRequest.getConceptsToTransform()) {
				if (batchJob == null) {
					batchJob = new ArrayList<>();
				}
				batchJob.add(conceptId);
				counter++;
				if (counter % batchMax == 0 || counter == transformRequest.getConceptsToTransform().size()) {
					// Do work
					final List<String> task = batchJob;
					results.add(executorService.submit(() -> batchTransform(input, task, restClient)));
					batchJob = null;
				}
			}
		} catch (IOException e) {
			String templateName = source == null ? transformRequest.getSourceTemplate() : destinationTemplate;
			throw new ServiceException("Failed to load template " + templateName, e);
		}
		return results;
		
	}
	
	private TransformationResult batchTransform(TransformationInputData input, List<String> conceptIds, SnowOwlRestClient restClient) {
		TransformationResult result = new TransformationResult();
		Map<String, String> errors = new HashMap<>();
		result.setFailures(errors);
		try {
			final List<ConceptPojo> conceptPojos = restClient.searchConcepts(input.getBranchPath(), conceptIds);
			if (conceptPojos != null) {
				List<String> missing = new ArrayList<>(conceptIds);
				for (ConceptPojo pojo : conceptPojos) {
					missing.remove(pojo.getConceptId());
					ConceptPojo transformed;
					try {
						transformed = performTransform(pojo, input, restClient);
						result.addTransformedConcept(transformed);
					} catch (ServiceException e) {
						errors.put(pojo.getConceptId(), e.getMessage());
					}
				}
				
				for (String conceptId : missing) {
					errors.put(conceptId, String.format("Failed to find concept %s from branch %s ", conceptId, input.getBranchPath()));
				}
			}
		} catch (RestClientException e) {
			String msg = String.format("Failed to load concepts %s from branch %s ", conceptIds, input.getBranchPath());
			if (e.getMessage() != null) {
				msg = msg + " caused by " + e.getMessage();
			}
			LOGGER.error(msg, e);
			errors.put("Error", msg);
		}
		return result;
	}
	
	private ConceptPojo performTransform(ConceptPojo conceptPojo, TransformationInputData inputData, SnowOwlRestClient restClient) throws ServiceException {
		ConceptPojo transformed = conceptPojo;
		ConceptTemplate conceptTemplate = inputData.getDestinationTemplate();
		Map<String, ConceptMiniPojo> attributeSlotValueMap;
		try {
			attributeSlotValueMap = constructSlotToTargetValueMap(inputData, conceptPojo, restClient);
		} catch (RestClientException e) {
			throw new ServiceException("Fail to validate slot target values" , e);
		}
		if (inputData.getTransformRequest().isLogicalTransform()) {
			org.ihtsdo.otf.rest.client.terminologyserver.pojo.DefinitionStatus definitionStatus = org.ihtsdo.otf.rest.client.terminologyserver.pojo.DefinitionStatus.PRIMITIVE;
			if (DefinitionStatus.FULLY_DEFINED == conceptTemplate.getConceptOutline().getDefinitionStatus()) {
				definitionStatus =  org.ihtsdo.otf.rest.client.terminologyserver.pojo.DefinitionStatus.FULLY_DEFINED;
			}
			transformed.setDefinitionStatus(definitionStatus);
			RelationshipTransformer relationShipTransformer = new RelationshipTransformer(transformed, conceptTemplate.getConceptOutline(), attributeSlotValueMap, inputData.getConceptIdMap());
			relationShipTransformer.transform();
		}
		
		if (inputData.getTransformRequest().isLexicalTransform()) {
			Map<String, Set<DescriptionPojo>> slotDescriptionsMap = getSlotDescriptionValuesMap(inputData.getBranchPath(), attributeSlotValueMap, restClient);
			DescriptionTransformer transformer = new DescriptionTransformer(transformed, conceptTemplate, slotDescriptionsMap, 
					inputData.getTransformRequest().getInactivationReason());
			transformer.transform();
			transformed.setEffectiveTime(null);
		}
		return transformed;
	}

	private Map<String, ConceptMiniPojo> constructSlotToTargetValueMap(TransformationInputData inputData, ConceptPojo conceptPojo, SnowOwlRestClient restClient) throws RestClientException {
		Map<String, Set<ConceptMiniPojo>> slotToAttrbuteValuesMap = TemplateUtil.getSlotNameToAttributeValueMap(inputData.getDestinationSlotToAttributeMap(), conceptPojo);
		// validate using attribute slot range when there is more than one value for a given slot
		Map<String, ConceptMiniPojo> slotToValuesMap = new HashMap<>();
		for (String slot : slotToAttrbuteValuesMap.keySet()) {
			List<String> conceptIds = slotToAttrbuteValuesMap.get(slot).stream().map(ConceptMiniPojo :: getConceptId).collect(Collectors.toList());
			if (conceptIds.size() > 1) {
				String rangeEcl = TemplateUtil.constructRangeValidationEcl(inputData.getDestinationSlotToAttributeMap().get(slot).getAllowableRangeECL().trim(), conceptIds);
				Set<String> conceptsWithinRange = restClient.eclQuery(inputData.getBranchPath(), rangeEcl, conceptIds.size());
				for (ConceptMiniPojo pojo : slotToAttrbuteValuesMap.get(slot)) {
					if (conceptsWithinRange.contains(pojo.getConceptId())) {
						slotToValuesMap.put(slot, pojo);
						break;
					}
				}
			} else {
				slotToValuesMap.put(slot, slotToAttrbuteValuesMap.get(slot).iterator().next());
			}
		}
		return slotToValuesMap;
	}

	private Map<String, ConceptMiniPojo> getDestinationConceptsMap(String branchPath, SnowOwlRestClient client, ConceptTemplate destination) throws ServiceException {
		List<String> conceptIds = new ArrayList<>();
		List<Relationship> relationships = destination.getConceptOutline().getClassAxioms().stream().findFirst().get().getRelationships();
		for (Relationship rel : relationships) {
			if (rel.getType() != null) {
				conceptIds.add(rel.getType().getConceptId());
			}
			if (rel.getTarget() != null) {
				conceptIds.add(rel.getTarget().getConceptId());
			}
		}
		LOGGER.info("Load concepts " + conceptIds  + " from branch " + branchPath);
		try {
			Set<ConceptMiniPojo> results = client.getConceptMinis(branchPath, conceptIds, conceptIds.size());
			Map<String, ConceptMiniPojo> conceptIdMap = new HashMap<>();
			for (ConceptMiniPojo pojo : results) {
				conceptIdMap.put(pojo.getConceptId(), pojo);
			}
			return conceptIdMap;
		} catch (RestClientException e) {
			throw new ServiceException("Failed to get concepts from branch " + branchPath , e);
		}
	}
	
	public void validate(ConceptTemplate source, ConceptTemplate destination) throws ServiceException {
		TemplateUtil.validateTermSlots(destination, false);
		try {
			LogicalTemplate sourcelogical = parser.parseTemplate(source.getLogicalTemplate());
			LogicalTemplate destinationLogical = parser.parseTemplate(destination.getLogicalTemplate());
			
			Set<String> sourceAttributeTypes = TemplateUtil.getAttributeTypes(sourcelogical);
			Map<String, Attribute> destinationSlotToAttributeMap = TemplateUtil.getSlotToAttributeMap(destinationLogical, false);
			
			Set<String> destinationTypes = destinationSlotToAttributeMap.values().stream().map(Attribute:: getType).collect(Collectors.toSet());
			// check mandatory destination attribute types exist in the source template
			if (!sourceAttributeTypes.containsAll(destinationTypes)) {
				StringBuilder msgBuilder = new StringBuilder();
				int counter = 0;
				for (String type : destinationTypes) {
					if (!sourceAttributeTypes.contains(type)) {
						if (counter++ > 0) {
							msgBuilder.append(",");
						}
						msgBuilder.append(type);
					}
				}
				throw new ServiceException(String.format("Destination template %s has slot attribute type %s that doesn't exist in the source template %s",
						destination.getName(), msgBuilder.toString(), source.getName()));
			}
		} catch (IOException e) {
			throw new ServiceException("Failed to parse logical template", e);
		}
	}

	private Map<String, Set<DescriptionPojo>> getSlotDescriptionValuesMap(String branchPath, 
			Map<String, ConceptMiniPojo> attributeSlotMap, SnowOwlRestClient restClient) throws ServiceException {

		Map<String, Set<DescriptionPojo>> slotDescriptionMap = new HashMap<>();
		List<String> conceptIds = attributeSlotMap.values().stream().map(ConceptMiniPojo::getConceptId).collect(Collectors.toList());
		List<ConceptPojo> results;
		try {
			results = restClient.searchConcepts(branchPath, conceptIds);
		} catch (RestClientException e) {
			throw new ServiceException("Failed to search concepts on branch " + branchPath, e);
		}
		Map<String, ConceptPojo> conceptPojoMap = new HashMap<>();
		for (ConceptPojo pojo : results) {
			conceptPojoMap.put(pojo.getConceptId(), pojo);
		}
		for (String slot : attributeSlotMap.keySet()) {
			ConceptPojo pojo = conceptPojoMap.get(attributeSlotMap.get(slot).getConceptId());
			if (pojo != null) {
				slotDescriptionMap.put(slot, conceptPojoMap.get(attributeSlotMap.get(slot).getConceptId()).getDescriptions());
			}
		}
		return slotDescriptionMap;
	}

	public TemplateTransformation createTemplateTransformation(String branchPath, TemplateTransformRequest transformRequest) throws ServiceException {
		if (!transformRequest.isLexicalTransform() && !transformRequest.isLogicalTransform()) {
			throw new IllegalArgumentException("Should state at least one type of transformation but got " + transformRequest);
		}
		return new TemplateTransformation(branchPath, transformRequest);
	}

	public ConceptPojo transformConcept(String branchPath, TemplateTransformRequest request, ConceptPojo conceptToTransform, SnowOwlRestClient restClient) throws ServiceException {
		ConceptTemplate destination = null;
		LogicalTemplate logical;
		String destinationTemplate = request.getDestinationTemplate();
		try {
			destination = templateService.loadOrThrow(destinationTemplate);
			logical = parser.parseTemplate(destination.getLogicalTemplate());
		} catch (ResourceNotFoundException | IOException e) {
			
			if (destination == null) {
				throw new IllegalArgumentException("No template found with name " + destinationTemplate, e);
			}
			throw new ServiceException("Failed to load and parse template " + destinationTemplate, e);
		}

		TransformationInputData inputData = new TransformationInputData(request);
		inputData.setBranchPath(branchPath);
		inputData.setDestinationTemplate(destination);
		inputData.setConceptIdMap(getDestinationConceptsMap(branchPath, restClient, destination));
		inputData.setDestinationSlotToAttributeMap(TemplateUtil.getSlotToAttributeMap(logical, true));
		
		return performTransform(conceptToTransform, inputData, restClient);
	}
}
