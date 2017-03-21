package org.ihtsdo.otf.authoringtemplate.rest;

import io.kaicode.rest.util.branchpathrewrite.BranchPathUriUtil;
import org.ihtsdo.otf.authoringtemplate.domain.ConceptOutline;
import org.ihtsdo.otf.authoringtemplate.domain.ConceptTemplate;
import org.ihtsdo.otf.authoringtemplate.rest.util.ControllerHelper;
import org.ihtsdo.otf.authoringtemplate.service.TemplateService;
import org.ihtsdo.otf.authoringtemplate.service.exception.ResourceNotFoundException;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@SuppressWarnings("unused")
public class TemplateController {

	@Autowired
	private TemplateService templateService;

	@RequestMapping(value = "/templates", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> createTemplate(@RequestParam String name, @RequestBody ConceptTemplate conceptTemplate) throws IOException {
		templateService.create(name, conceptTemplate);
		return ControllerHelper.getCreatedResponse(name);
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
	public ConceptTemplate getTemplate(@PathVariable String templateName) throws IOException {
		return templateService.load(templateName);
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

}
