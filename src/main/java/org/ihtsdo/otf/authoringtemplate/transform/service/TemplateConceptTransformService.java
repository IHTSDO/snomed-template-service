package org.ihtsdo.otf.authoringtemplate.transform.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.ihtsdo.otf.authoringtemplate.service.Constants;
import org.ihtsdo.otf.authoringtemplate.service.TemplateService;
import org.ihtsdo.otf.authoringtemplate.service.TemplateUtil;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.authoringtemplate.transform.DescriptionTransformer;
import org.ihtsdo.otf.authoringtemplate.transform.RelationshipTransformer;
import org.ihtsdo.otf.authoringtemplate.transform.TemplateTransformRequest;
import org.ihtsdo.otf.authoringtemplate.transform.TemplateTransformation;
import org.ihtsdo.otf.authoringtemplate.transform.TransformationInputData;
import org.ihtsdo.otf.authoringtemplate.transform.TransformationResult;
import org.ihtsdo.otf.authoringtemplate.transform.TransformationStatus;
import org.ihtsdo.otf.rest.client.RestClientException;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClient;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptMiniPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.SimpleConceptPojo;
import org.ihtsdo.otf.rest.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.DefinitionStatus;
import org.snomed.authoringtemplate.domain.Relationship;
import org.snomed.authoringtemplate.domain.SimpleSlot;
import org.snomed.authoringtemplate.domain.logical.LogicalTemplate;
import org.snomed.authoringtemplate.service.LogicalTemplateParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TemplateConceptTransformService {

	@Autowired
	private TemplateService templateService;
	
	@Autowired
	private TemplateTransformationResultService resultService;
	
	private final ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateConceptTransformService.class);

	
	@Value("${transformation.batch.max}")
	private int batchMax;
	
	@Async
	public void transformAsynchnously(TemplateTransformation transformation, SnowOwlRestClient restClient) throws ServiceException {
		transformation.setStatus(TransformationStatus.RUNNING);
		resultService.update(transformation);
		List<Future<TransformationResult>> results = null;
		try {
			results = transform(transformation, restClient);
		} catch (Exception e) {
			LOGGER.error("Error occurred", e);
			transformation.setStatus(TransformationStatus.FAILED);
			transformation.setErrorMsg(getErrorMsg(e));
			resultService.update(transformation);
			return;
		}
		List<ConceptPojo> transformed = new ArrayList<>();
		Map<String, String> errorMsgMap = new HashMap<>();
		for (Future<TransformationResult> future : results) {
			try {
				TransformationResult transformationResult = future.get();
				transformed.addAll(transformationResult.getConcepts());
				for (String key : transformationResult.getFailures().keySet()) {
					errorMsgMap.put(key, transformationResult.getFailures().get(key));
				}
			} catch (InterruptedException | ExecutionException e) {
				String errorMsg = "Unexpected errors while merging results.";
				LOGGER.error(errorMsg, e);
				transformation.setStatus(TransformationStatus.FAILED);
				transformation.setErrorMsg(errorMsg + getErrorMsg(e));
			}
		}
		if (errorMsgMap.isEmpty()) {
			transformation.setStatus(TransformationStatus.COMPLETED);
		} else {
			transformation.setStatus(TransformationStatus.COMPLETED_WITH_FAILURE);
		}
		TransformationResult finalResult = new TransformationResult();
		finalResult.setConcepts(transformed);
		finalResult.setFailures(errorMsgMap);
		resultService.writeResultsToFile(transformation, finalResult);
		resultService.update(transformation);
	}
	
	private String getErrorMsg(Throwable t) {
		if (t instanceof ServiceException) {
			return t.getMessage();
		} else {
			if (t.getMessage() != null) {
				return t.getMessage();
			} else {
				return "Unexpected error caused by " + t.getCause().getMessage();
			}
		}
	}
	
	
	private TransformationInputData constructTransformationInputData(ConceptTemplate destination, TemplateTransformRequest transformRequest) throws ServiceException {
		TransformationInputData input = new TransformationInputData();
		input.setInactivationReason(transformRequest.getInactivationReason());
		LogicalTemplateParserService parser = new LogicalTemplateParserService();
		LogicalTemplate logical;
		try {
			logical = parser.parseTemplate(destination.getLogicalTemplate());
			input.setDestinationAttributeTypeSlotMap(TemplateUtil.getAttributeTypeSlotMap(logical));
			
		} catch (IOException e) {
			throw new ServiceException("Failed to parse logical template " + destination.getName(), e);
		}
		input.setDestinationTemplate(destination);
		return input;
	}

	public List<Future<TransformationResult>> transform(TemplateTransformation transformation, SnowOwlRestClient restClient) throws ServiceException {
		
		String branchPath = transformation.getBranchPath();
		String destinationTemplate = transformation.getDestinationTemplate();
		TemplateTransformRequest transformRequest = transformation.getTransformRequest();
		
		List<Future<TransformationResult>> results = new ArrayList<>();
		try {
			ConceptTemplate source = templateService.loadOrThrow(transformRequest.getSourceTemplate());
			ConceptTemplate destination = templateService.loadOrThrow(destinationTemplate);
			validate(source, destination);
			Map<String, SimpleConceptPojo> conceptMap = null;
			try {
				conceptMap = getDestinationConceptsMap(branchPath, restClient, destination);
			} catch (RestClientException e) {
				throw new ServiceException("Failed to get concepts from branch " + branchPath , e);
			}
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
					//do work
					final List<String> task = batchJob;
					results.add(executorService.submit(() -> batchTransform(input, task, restClient)));
					batchJob = null;
				}
			}
		} catch ( IOException e) {
			throw new ServiceException("Failed to load template " + destinationTemplate, e);
		}
		return results;
		
	}

	
	private TransformationResult batchTransform(TransformationInputData input, List<String> conceptIds, SnowOwlRestClient restClient) {
		TransformationResult result = new TransformationResult();
		String branchPath = input.getBranchPath();
		String inactivationReason = input.getInactivationReason();
		Map<String, String> errors = new HashMap<>();
		result.setFailures(errors);
		List<ConceptPojo> conceptPojos = null;
		try {
			conceptPojos = restClient.searchConcepts(branchPath, conceptIds);
		} catch (RestClientException e) {
			String msg = String.format("Failed to load concepts %s from branch %s ", conceptIds, branchPath);
			if (e.getMessage() != null) {
				msg = msg + " caused by " + e.getMessage();
			}
			LOGGER.warn(msg, e);
			errors.put("Error", msg);
		}
		
		if (conceptPojos != null) {
			List<String> missing = new ArrayList<>(conceptIds);
			for (ConceptPojo pojo : conceptPojos) {
				missing.remove(pojo.getConceptId());
				Map<String, ConceptMiniPojo> attributeSlotMap = TemplateUtil.getAttributeSlotValueMap(input.getDestinationAttributeTypeSlotMap(), pojo);
				ConceptPojo transformed = null;
				try {
					transformed = performTransform(pojo, input.getDestinationTemplate(), attributeSlotMap, inactivationReason, input.getConceptIdMap());
					result.addTransformedConcept(transformed);
				} catch (ServiceException e) {
					errors.put(pojo.getConceptId(), e.getMessage());
				}
			}
			for (String conceptId : missing) {
				errors.put(conceptId, String.format("Failed to find concept %s from branch %s ", conceptId, branchPath));
			}
		}
		return result;
	}
	
	private Map<String, SimpleConceptPojo> getDestinationConceptsMap(String branchPath, SnowOwlRestClient client, ConceptTemplate destination) throws RestClientException {
		List<String> conceptIds = new ArrayList<>();
		for (Relationship rel : destination.getConceptOutline().getRelationships()) {
			if (rel.getType() != null) {
				conceptIds.add(rel.getType().getConceptId());
			}
			if (rel.getTarget() != null) {
				conceptIds.add(rel.getTarget().getConceptId());
			}
		}
		LOGGER.info("Load concepts " + conceptIds  + " from branch " + branchPath);
		Set<SimpleConceptPojo> results = client.getConcepts(branchPath, null, null, conceptIds, conceptIds.size());
		Map<String, SimpleConceptPojo> conceptIdMap = new HashMap<>();
		for (SimpleConceptPojo pojo : results) {
			conceptIdMap.put(pojo.getId(), pojo);
		}
		return conceptIdMap;
	}

	private Set<String> getLogicalReplacementSlotsFromTemplate(ConceptTemplate conceptTemplate) throws IOException {
		List<SimpleSlot> slotsRequired = TemplateUtil.getSlotsRequiringInput(conceptTemplate.getConceptOutline().getRelationships());
		Set<String> slotAttributeTypes = slotsRequired.stream().map(s -> s.getSlotName()).collect(Collectors.toSet());
		return slotAttributeTypes;
	}
	
	public void validate(ConceptTemplate source, ConceptTemplate destination) throws ServiceException, IOException {
		
		//check term slots can be found in replacement slots
		Set<String> termTemplates = TemplateUtil.getTermTemplates(destination);
		Set<String> termSlots = TemplateUtil.getSlots(termTemplates.toArray(new String[termTemplates.size()]));
		Map<String, String> lexicalTermNameSlotMap = TemplateUtil.getLexicalTermNameSlotMap(destination);
		//Check term names in term templates are defined in the lexical templates
		if (!lexicalTermNameSlotMap.keySet().containsAll(termSlots)) {
			Set<String> slotsNotFound = termSlots;
			slotsNotFound.removeAll(lexicalTermNameSlotMap.keySet());
			throw new ServiceException(String.format("Template %s has term slot %s that is not defined in the lexical template",
					destination.getName(), slotsNotFound));
		}
		
		Set<String> destinationSlots = getLogicalReplacementSlotsFromTemplate(destination);
		Set<String> slotsReferencedInLexical = new HashSet<>(lexicalTermNameSlotMap.values());
		if (!destinationSlots.containsAll(slotsReferencedInLexical)) {
			Set<String> slotsNotFound = slotsReferencedInLexical;
			slotsNotFound.removeAll(destinationSlots);
			throw new ServiceException(String.format("Destination template %s has slot referenced in the lexical template %s that doesn't exist in the logical template",
					destination.getName(), slotsNotFound));
		}
		
		try {
			LogicalTemplateParserService parser = new LogicalTemplateParserService();
			LogicalTemplate sourcelogical = parser.parseTemplate(source.getLogicalTemplate());
			LogicalTemplate destinationLogical = parser.parseTemplate(destination.getLogicalTemplate());
			Set<String> sourceAttributeTypes = TemplateUtil.getAttributeTypes(sourcelogical);
			Map<String, Set<String>> destinationAttribyteSlotMap = TemplateUtil.getAttributeTypeSlotMap(destinationLogical);
			Set<String> destinationTypes = destinationAttribyteSlotMap.keySet();
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

	private ConceptPojo performTransform(ConceptPojo conceptPojo,
										 ConceptTemplate conceptTemplate, 
										 Map<String, ConceptMiniPojo> attributeSlotMap, 
										 String inactivationReason,
										 Map<String, SimpleConceptPojo> conceptIdMap) throws ServiceException {
		ConceptPojo transformed = conceptPojo;
		org.ihtsdo.otf.rest.client.snowowl.pojo.DefinitionStatus definitionStatus = org.ihtsdo.otf.rest.client.snowowl.pojo.DefinitionStatus.PRIMITIVE;
		if (DefinitionStatus.FULLY_DEFINED == conceptTemplate.getConceptOutline().getDefinitionStatus()) {
			definitionStatus =  org.ihtsdo.otf.rest.client.snowowl.pojo.DefinitionStatus.FULLY_DEFINED;
		}
		transformed.setDefinitionStatus(definitionStatus);
		RelationshipTransformer relationShipTransformer = new RelationshipTransformer(transformed, conceptTemplate.getConceptOutline(), attributeSlotMap, conceptIdMap);
		relationShipTransformer.transform();
		Map<String, String> slotValueMap = getSlotDescriptionValueMap(attributeSlotMap);
		DescriptionTransformer transformer = new DescriptionTransformer(transformed, conceptTemplate, slotValueMap, inactivationReason);
		transformer.transform();
		transformed.setEffectiveTime(null);
		return transformed;
	}


	private Map<String, String> getSlotDescriptionValueMap(Map<String, ConceptMiniPojo> attributeSlotMap) {
		Map<String, String> slotDescriptionMap = new HashMap<>();
		for (String slotName : attributeSlotMap.keySet()) {
			slotDescriptionMap.put(slotName, TemplateUtil.getDescriptionFromFSN(attributeSlotMap.get(slotName).getFsn()));
		}
		return slotDescriptionMap;
	}

	public TemplateTransformation createTemplateTransformation(String branchPath, String destinationTemplate,
			TemplateTransformRequest transformRequest) throws ServiceException {
		TemplateTransformation templateTransformation = new TemplateTransformation(branchPath, destinationTemplate, transformRequest);
		return templateTransformation;
	}

	public ConceptPojo transformConcept(String branchPath, String destinationTemplate, ConceptPojo conceptToTransform, SnowOwlRestClient restClient) throws ServiceException {
		ConceptTemplate destination = null;
		LogicalTemplate logical = null;
		try {
			destination = templateService.loadOrThrow(destinationTemplate);
			LogicalTemplateParserService parser = new LogicalTemplateParserService();
			logical = parser.parseTemplate(destination.getLogicalTemplate());
		} catch (ResourceNotFoundException | IOException e) {
			throw new ServiceException("Failed to load and parse logical template " + destinationTemplate, e);
		}
		Map<String, SimpleConceptPojo> conceptMap = null;
		try {
			conceptMap = getDestinationConceptsMap(branchPath, restClient, destination);
		} catch (RestClientException e) {
			throw new ServiceException("Failed to get concepts from branch " + branchPath , e);
		}
		
		Map<String, ConceptMiniPojo> attributeSlotMap = TemplateUtil.getAttributeSlotValueMap(
				TemplateUtil.getAttributeTypeSlotMap(logical), conceptToTransform);
		return performTransform(conceptToTransform, destination, attributeSlotMap, Constants.NONCONFORMANCE, conceptMap);
	}
}
