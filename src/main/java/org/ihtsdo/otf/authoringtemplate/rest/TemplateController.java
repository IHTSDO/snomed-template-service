package org.ihtsdo.otf.authoringtemplate.rest;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.ihtsdo.otf.authoringtemplate.rest.util.ControllerHelper;
import org.ihtsdo.otf.authoringtemplate.service.TemplateConceptCreateService;
import org.ihtsdo.otf.authoringtemplate.service.TemplateConceptSearchService;
import org.ihtsdo.otf.authoringtemplate.service.TemplateService;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.authoringtemplate.transform.TemplateTransformRequest;
import org.ihtsdo.otf.authoringtemplate.transform.TemplateTransformation;
import org.ihtsdo.otf.authoringtemplate.transform.TransformationResult;
import org.ihtsdo.otf.authoringtemplate.transform.TransformationStatus;
import org.ihtsdo.otf.authoringtemplate.transform.service.TemplateConceptTransformService;
import org.ihtsdo.otf.authoringtemplate.transform.service.TemplateTransformationResultService;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowOwlRestClient;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowOwlRestClientFactory;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.exception.ResourceNotFoundException;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import io.kaicode.rest.util.branchpathrewrite.BranchPathUriUtil;

@RestController
public class TemplateController {

	@Autowired
	private TemplateService templateService;
	
	@Autowired
	private TemplateConceptCreateService createService;
	
	@Autowired
	private TemplateConceptSearchService searchService;
	
	@Autowired
	private TemplateConceptTransformService transformService;
	
	@Autowired
	private SnowOwlRestClientFactory terminologyClientFactory;
	
	@Autowired
	private TemplateTransformationResultService resultService;
	
	@RequestMapping(value = "/templates", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> createTemplate(@RequestParam String templateName, @RequestBody ConceptTemplate conceptTemplate) throws IOException, ServiceException {
		templateService.create(templateName, conceptTemplate);
		return ControllerHelper.getCreatedResponse(templateName);
	}

	@RequestMapping(value = "/templates/{templateName}", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public ConceptTemplate updateTemplate(@PathVariable String templateName, @RequestBody ConceptTemplate conceptTemplate) throws IOException, ServiceException {
		return templateService.update(templateName, conceptTemplate);
	}

	@RequestMapping(value = "/templates", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Set<ConceptTemplate> listTemplates() throws IOException {
		return templateService.listAll();
	}

	@RequestMapping(value = "/{branchPath}/templates", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Set<ConceptTemplate> listTemplates(@PathVariable String branchPath,
											  @RequestParam(required = false) String[] descendantOf,
											  @RequestParam(required = false) String[] ancestorOf) throws IOException {
		return templateService.listAll(BranchPathUriUtil.decodePath(branchPath),
				descendantOf, ancestorOf);
	}

	@RequestMapping(value = "/templates/{templateName}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ConceptTemplate getTemplate(@PathVariable String templateName) throws ResourceNotFoundException, IOException {
		ConceptTemplate template = templateService.load(templateName);
		if (template == null) {
			throw new ResourceNotFoundException("Template", templateName);
		}
		return template;
	}

	@RequestMapping(value = "/{branchPath}/templates/{templateName}/empty-input-file", method = RequestMethod.GET,
			produces = "text/tab-separated-values; charset=utf-8")
	public void getEmptyInputFile(@PathVariable String branchPath, @PathVariable String templateName,
								  HttpServletResponse response) throws IOException, ResourceNotFoundException {

		response.setContentType("text/tab-separated-values; charset=utf-8");
		templateService.writeEmptyInputFile(BranchPathUriUtil.decodePath(branchPath), templateName, response.getOutputStream());
	}

	@RequestMapping(value = "/{branchPath}/templates/{templateName}/generate", method = RequestMethod.POST, consumes = "multipart/form-data")
	@ResponseBody
	public List<ConceptOutline> generateConcepts(@PathVariable String branchPath,
												 @PathVariable String templateName,
												 @RequestParam("tsvFile") MultipartFile tsvFile) throws IOException, ServiceException {
		return createService.generateConcepts(BranchPathUriUtil.decodePath(branchPath), templateName, tsvFile.getInputStream());
	}

	@RequestMapping(value = "/templates/reload", method = RequestMethod.POST)
	public void reloadCache() throws IOException, ServiceException {
		templateService.reloadCache();
	}
	
	@RequestMapping(value = "/{branchPath}/templates/{templateName}/concepts", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Set<String> searchConcepts(@PathVariable String branchPath,
									  @PathVariable String templateName,
									  @RequestParam Boolean logicalMatch,
									  @RequestParam(required=false) Boolean lexicalMatch,
									  @RequestParam(defaultValue="true") boolean stated) throws IOException, ServiceException {
		return searchService.searchConceptsByTemplate(templateName, BranchPathUriUtil.decodePath(branchPath), logicalMatch, lexicalMatch, stated);
	}
	
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{branchPath}/templates/transform", method = RequestMethod.POST)
	public ResponseEntity createTemplateTransformation(@PathVariable String branchPath,
									@RequestBody TemplateTransformRequest transformRequest,
									UriComponentsBuilder uriComponentsBuilder
									) throws ServiceException {
		TemplateTransformation transformation = transformService.createTemplateTransformation(BranchPathUriUtil.decodePath(branchPath), transformRequest);
		SnowOwlRestClient restClient = terminologyClientFactory.getClient();
		transformService.transformAsynchnously(transformation, restClient);
		transformation.setStatus(TransformationStatus.QUEUED);
		resultService.update(transformation);
		return ResponseEntity.created(uriComponentsBuilder.path("/templates/transform/{transformationId}")
				.buildAndExpand(transformation.getTransformationId()).toUri()).build();
	}
	
	@RequestMapping(value = "/{branchPath}/templates/transform/concept", method = RequestMethod.POST)
	@ResponseBody
	public ConceptPojo transformConceptToTemplate(@PathVariable String branchPath,
									@RequestParam String destinationTemplate,
									@RequestBody ConceptPojo conceptToTransform) throws ServiceException {
		if (conceptToTransform == null) {
			throw new IllegalArgumentException("Concept to be transformed must not be null " + conceptToTransform);
		}
		
		if (conceptToTransform.getDescriptions() == null) {
			throw new IllegalArgumentException("Concept to be transformed must not have null descriptions " + conceptToTransform);
		}
		
		if (conceptToTransform.getClassAxioms() == null) {
			throw new IllegalArgumentException("The class axioms to be transformed must not be null " + conceptToTransform);
		}
		SnowOwlRestClient restClient = terminologyClientFactory.getClient();
		TemplateTransformRequest request = new TemplateTransformRequest();
		request.setDestinationTemplate(destinationTemplate);
		return transformService.transformConcept(BranchPathUriUtil.decodePath(branchPath), request, conceptToTransform, restClient);
	}
	
	@RequestMapping(value = "/templates/transform/{transformationId}", method = RequestMethod.GET)
	@ResponseBody
	public TemplateTransformation getTransformationStatus(@PathVariable String transformationId) throws ServiceException {
		return resultService.getTemplateTransformation(transformationId);
	}
	
	@RequestMapping(value = "/templates/transform/{transformationId}/results/", method = RequestMethod.GET)
	@ResponseBody
	public TransformationResult getTransformationResults(@PathVariable String transformationId) throws ServiceException {
		return resultService.getResult(transformationId);
	}
}
