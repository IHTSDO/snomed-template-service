package org.ihtsdo.otf.authoringtemplate.transform.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.ihtsdo.otf.authoringtemplate.domain.ConceptOutline;
import org.ihtsdo.otf.authoringtemplate.domain.ConceptTemplate;
import org.ihtsdo.otf.authoringtemplate.domain.DefinitionStatus;
import org.ihtsdo.otf.authoringtemplate.domain.DescriptionType;
import org.ihtsdo.otf.authoringtemplate.domain.Relationship;
import org.ihtsdo.otf.authoringtemplate.domain.TemplateTransformation;
import org.ihtsdo.otf.authoringtemplate.domain.logical.Attribute;
import org.ihtsdo.otf.authoringtemplate.domain.logical.AttributeGroup;
import org.ihtsdo.otf.authoringtemplate.domain.logical.LogicalTemplate;
import org.ihtsdo.otf.authoringtemplate.service.LogicalTemplateParserService;
import org.ihtsdo.otf.authoringtemplate.service.TemplateService;
import org.ihtsdo.otf.authoringtemplate.service.TemplateUtil;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.authoringtemplate.transform.DescriptionTransformer;
import org.ihtsdo.otf.authoringtemplate.transform.RelationshipTransformer;
import org.ihtsdo.otf.authoringtemplate.transform.TemplateTransformRequest;
import org.ihtsdo.otf.authoringtemplate.transform.TransformationInputData;
import org.ihtsdo.otf.authoringtemplate.transform.TransformationResult;
import org.ihtsdo.otf.authoringtemplate.transform.TransformationStatus;
import org.ihtsdo.otf.rest.client.RestClientException;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClient;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptMiniPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ConceptTemplateTransformService {

	@Autowired
	private TemplateService templateService;
	
	@Autowired
	private TemplateTransformationResultService resultService;
	
	private final ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConceptTemplateTransformService.class);

	private static final int BATCH_SIZE = 10;
	
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
			return;
		}
		List<ConceptPojo> transformed = new ArrayList<>();
		Map<String, String> errorMsgMap = new HashMap<>();
		for (Future<TransformationResult> future : results) {
			try {
				TransformationResult transformationResult = future.get();
				transformed.addAll(transformationResult.getTransformedConcepts());
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
		
		if (!errorMsgMap.isEmpty() && !transformed.isEmpty()) {
			transformation.setStatus(TransformationStatus.COMPLETED_WITH_FAILURE);
		} else {
			transformation.setStatus(TransformationStatus.COMPLETED);
		}
		TransformationResult finalResult = new TransformationResult();
		finalResult.setTransformedConcepts(transformed);
		finalResult.setErrors(errorMsgMap);
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
	
	
	private TransformationInputData constructTransformationInputData(ConceptTemplate source, ConceptTemplate destination) throws ServiceException {
		Set<String> termTemplates = TemplateUtil.getTermTemplates(source, DescriptionType.SYNONYM);
		Set<String> fsnTemplates = TemplateUtil.getTermTemplates(source, DescriptionType.FSN);
		Map<Pattern, List<String>> synonymPatterns = TemplateUtil.compilePatterns(termTemplates);
		Map<Pattern, List<String>> fsnPatterns = TemplateUtil.compilePatterns(fsnTemplates);
		
		TransformationInputData input = new TransformationInputData();
		input.setSynonymTemplates(termTemplates);
		input.setFsnTemplates(fsnTemplates);
		input.setSynonymPatterns(synonymPatterns);
		input.setFsnPatterns(fsnPatterns);
		LogicalTemplateParserService parser = new LogicalTemplateParserService();
		LogicalTemplate logical;
		try {
			logical = parser.parseTemplate(source.getLogicalTemplate());
			input.setSourceAttributeTypeSlotMap(TemplateUtil.getAttributeTypeSlotMap(logical));
			input.setSourceLogicalTemplate(logical);
			
		} catch (IOException e) {
			throw new ServiceException("Failed to parse source logical template", e);
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
			Map<String, String> conceptFsnMap = null;
			try {
				conceptFsnMap = getDestinationConceptFsnMap(branchPath, restClient, destination);
			} catch (RestClientException e) {
				throw new ServiceException("Failed to get FSNs" , e);
			}
			final TransformationInputData input = constructTransformationInputData(source, destination);
			input.setBranchPath(branchPath);
			input.setConceptFsnMap(conceptFsnMap);
			List<String> batchJob = null;
			int counter=0;
			for (String conceptId : transformRequest.getConceptsToTransform()) {
				if (batchJob == null) {
					batchJob = new ArrayList<>();
				}
				batchJob.add(conceptId);
				counter++;
				if (counter % BATCH_SIZE == 0 || counter == transformRequest.getConceptsToTransform().size()) {
					//do work
					final List<String> task = batchJob;
					results.add(executorService.submit(new Callable<TransformationResult>() {
						@Override
						public TransformationResult call() throws Exception {
							
							return batchTransform(input, task, restClient);
						}
					}));
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
		result.setErrors(errors);
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
				Map<String, String> slotValueMap = TemplateUtil.getSlotValueMap(input.getFsnPatterns(), input.getSynonymPatterns(), pojo);
				Map<String, ConceptMiniPojo> attributeSlotMap = TemplateUtil.getAttributeSlotValueMap(input.getSourceAttributeTypeSlotMap(), pojo);
				Map<String, String> conceptFsnMap = input.getConceptFsnMap();
				ConceptPojo transformed = performTransform(pojo, input.getDestinationTemplate().getConceptOutline(), slotValueMap, attributeSlotMap, inactivationReason, conceptFsnMap);
				result.addTransformedConcept(transformed);
				
			}
			for (String conceptid : missing) {
				errors.put(conceptid, String.format("Failed to find concept %s from branch %s ", conceptIds, branchPath));
			}
		}
		return result;
	}
	
	private Map<String, String> getDestinationConceptFsnMap(String branchPath, SnowOwlRestClient client, ConceptTemplate destination) throws RestClientException {
		Set<String> conceptIds = new HashSet<>();
		for (Relationship rel : destination.getConceptOutline().getRelationships()) {
			if (rel.getType() != null) {
				conceptIds.add(rel.getType().getConceptId());
			}
			if (rel.getTarget() != null) {
				conceptIds.add(rel.getTarget().getConceptId());
			}
		}
		return client.getFsns(branchPath, conceptIds);
	}

	private Set<String> getSlotsFromTemplate(ConceptTemplate conceptTemplate) throws IOException {
		Set<String> slots = new HashSet<>();
		LogicalTemplateParserService parser = new LogicalTemplateParserService();
		LogicalTemplate logical = parser.parseTemplate(conceptTemplate.getLogicalTemplate());
		for (Attribute attr : logical.getUngroupedAttributes()) {
			if (attr.getSlotName() != null) {
				slots.add(attr.getSlotName());
			}
		}
		for (AttributeGroup attributeGrp : logical.getAttributeGroups()) {
			for (Attribute att : attributeGrp.getAttributes()) {
				if (att.getSlotName() != null) {
					slots.add(att.getSlotName());
				}
			}
		}
		return slots;
	}
	
	public void validate(ConceptTemplate source, ConceptTemplate destination) throws ServiceException, IOException {
		Set<String> sourceSlots = getSlotsFromTemplate(source);
		Set<String> destinationSlots = getSlotsFromTemplate(destination);
		LOGGER.info("Source slots {} destination slots {}", sourceSlots, destinationSlots);
		if (!sourceSlots.containsAll(destinationSlots)) {
			StringBuilder msgBuilder = new StringBuilder();
			int counter = 0;
			for (String slot : destinationSlots) {
				if (!sourceSlots.contains(slot)) {
					if (counter++ > 0) {
						msgBuilder.append(",");
					}
					msgBuilder.append(slot);
				}
			}
			throw new ServiceException(String.format("Destination template %s has slot %s that doesn't exist in the source template %s",
													destination.getName(), msgBuilder.toString(), source.getName()));
		}
	}

	private ConceptPojo performTransform(ConceptPojo conceptPojo,
										 ConceptOutline conceptOutline, 
										 Map<String, String> slotValueMap, 
										 Map<String, ConceptMiniPojo> attributeSlotMap, 
										 String inactivationReason,
										 Map<String,String> conceptFsnMap) {
		ConceptPojo transformed = conceptPojo;
		org.ihtsdo.otf.rest.client.snowowl.pojo.DefinitionStatus definitionStatus = org.ihtsdo.otf.rest.client.snowowl.pojo.DefinitionStatus.PRIMITIVE;
		if (DefinitionStatus.FULLY_DEFINED == conceptOutline.getDefinitionStatus()) {
			definitionStatus =  org.ihtsdo.otf.rest.client.snowowl.pojo.DefinitionStatus.FULLY_DEFINED;
		}
		transformed.setDefinitionStatus(definitionStatus);
		DescriptionTransformer transformer = new DescriptionTransformer(transformed, conceptOutline, slotValueMap, inactivationReason);
		transformer.transform();
		RelationshipTransformer relationShipTransformer = new RelationshipTransformer(transformed, conceptOutline, attributeSlotMap, conceptFsnMap);
		relationShipTransformer.tranform();
		return transformed;
	}


	public TemplateTransformation createTemplateTransformation(String branchPath, String destinationTemplate,
			TemplateTransformRequest transformRequest) throws ServiceException {
		TemplateTransformation templateTransformation = new TemplateTransformation(branchPath, destinationTemplate, transformRequest);
		return templateTransformation;
	}
}
