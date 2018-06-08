package org.ihtsdo.otf.authoringtemplate.rest;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.ihtsdo.otf.authoringtemplate.domain.ConceptOutline;
import org.ihtsdo.otf.authoringtemplate.domain.ConceptTemplate;
import org.ihtsdo.otf.authoringtemplate.rest.util.ControllerHelper;
import org.ihtsdo.otf.authoringtemplate.service.ConceptTemplateSearchService;
import org.ihtsdo.otf.authoringtemplate.service.ConceptTemplateTransformService;
import org.ihtsdo.otf.authoringtemplate.service.TemplateService;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
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
									  @RequestParam(value="stated", defaultValue="true") boolean stated) throws IOException, ServiceException {
		return searchService.searchConceptsByTemplate(templateName, BranchPathUriUtil.parseBranchPath(branchPath), logicalMatch, lexicalMatch, stated);
	}
	
	@RequestMapping(value = "/{branchPath}/templates/{templateName}/transform", method = RequestMethod.POST)
	@ResponseBody
	public List<ConceptPojo> transformConcepts(@PathVariable String branchPath,
											   @PathVariable String templateName,
											   @RequestParam String sourceTemplate,
											   @RequestParam Set<String> conceptsToTransform) throws ServiceException {
		return transformService.transform(BranchPathUriUtil.parseBranchPath(branchPath), sourceTemplate, conceptsToTransform, templateName);
	}
}
