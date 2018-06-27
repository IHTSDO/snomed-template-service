package org.ihtsdo.otf.authoringtemplate.rest;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.ihtsdo.otf.authoringtemplate.domain.ConceptOutline;
import org.ihtsdo.otf.authoringtemplate.domain.ConceptTemplate;
import org.ihtsdo.otf.authoringtemplate.domain.TemplateTransformation;
import org.ihtsdo.otf.authoringtemplate.rest.util.ControllerHelper;
import org.ihtsdo.otf.authoringtemplate.service.ConceptTemplateSearchService;
import org.ihtsdo.otf.authoringtemplate.service.TemplateService;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.authoringtemplate.transform.TemplateTransformRequest;
import org.ihtsdo.otf.authoringtemplate.transform.TransformationResult;
import org.ihtsdo.otf.authoringtemplate.transform.TransformationStatus;
import org.ihtsdo.otf.authoringtemplate.transform.service.ConceptTemplateTransformService;
import org.ihtsdo.otf.authoringtemplate.transform.service.TemplateTransformationResultService;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClient;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClientFactory;
import org.ihtsdo.otf.rest.exception.ResourceNotFoundException;
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
@SuppressWarnings("unused")
public class TemplateController {

	@Autowired
	private TemplateService templateService;
	
	@Autowired
	private ConceptTemplateSearchService searchService;
	
	@Autowired
	private ConceptTemplateTransformService transformService;
	
	@Autowired
	private SnowOwlRestClientFactory terminologyClientFactory;
	
	@Autowired
	private TemplateTransformationResultService resultService;
	
	@RequestMapping(value = "/templates", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> createTemplate(@RequestParam String templateName, @RequestBody ConceptTemplate conceptTemplate) throws IOException {
		templateService.create(templateName, conceptTemplate);
		return ControllerHelper.getCreatedResponse(templateName);
	}

	@RequestMapping(value = "/templates/{templateName}", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public ConceptTemplate updateTemplate(@PathVariable String templateName, @RequestBody ConceptTemplate conceptTemplate) throws IOException {
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
		return templateService.listAll(BranchPathUriUtil.parseBranchPath(branchPath),
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
		templateService.writeEmptyInputFile(BranchPathUriUtil.parseBranchPath(branchPath), templateName, response.getOutputStream());
	}

	@RequestMapping(value = "/{branchPath}/templates/{templateName}/generate", method = RequestMethod.POST, consumes = "multipart/form-data")
	@ResponseBody
	public List<ConceptOutline> generateConcepts(@PathVariable String branchPath,
												 @PathVariable String templateName,
												 @RequestParam("tsvFile") MultipartFile tsvFile) throws IOException, ServiceException {
		return templateService.generateConcepts(BranchPathUriUtil.parseBranchPath(branchPath), templateName, tsvFile.getInputStream());
	}

	@RequestMapping(value = "/templates/reload", method = RequestMethod.POST)
	public void reloadCache() throws IOException {
		templateService.reloadCache();
	}
	
	@RequestMapping(value = "/{branchPath}/templates/{templateName}/concepts", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Set<String> searchConcepts(@PathVariable String branchPath,
									  @PathVariable String templateName,
									  @RequestParam Boolean logicalMatch,
									  @RequestParam(required=false) Boolean lexicalMatch,
									  @RequestParam(defaultValue="true") boolean stated) throws IOException, ServiceException {
		return searchService.searchConceptsByTemplate(templateName, BranchPathUriUtil.parseBranchPath(branchPath), logicalMatch, lexicalMatch, stated);
	}
	
	
	private SnowOwlRestClient getRestClient() {
		return terminologyClientFactory.getClient();
	}
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{branchPath}/templates/{destinationTemplate}/transform", method = RequestMethod.POST)
	public ResponseEntity createTemplateTransformation(@PathVariable String branchPath,
									@PathVariable String destinationTemplate,
									@RequestBody TemplateTransformRequest transformRequest,
									UriComponentsBuilder uriComponentsBuilder
									) throws ServiceException {
		TemplateTransformation transformation = transformService.createTemplateTransformation(
				BranchPathUriUtil.parseBranchPath(branchPath), destinationTemplate, transformRequest);
		SnowOwlRestClient restClient = terminologyClientFactory.getClient();
		transformService.transformAsynchnously(transformation, restClient);
		transformation.setStatus(TransformationStatus.QUEUED);
		resultService.update(transformation);
		return ResponseEntity.created(uriComponentsBuilder.path("/templates/transform/{transformationId}")
				.buildAndExpand(transformation.getTransformationId()).toUri()).build();
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
