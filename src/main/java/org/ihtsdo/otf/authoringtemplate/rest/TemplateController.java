package org.ihtsdo.otf.authoringtemplate.rest;

import io.kaicode.rest.util.branchpathrewrite.BranchPathUriUtil;
import org.ihtsdo.otf.authoringtemplate.domain.ConceptTemplate;
import org.ihtsdo.otf.authoringtemplate.rest.util.ControllerHelper;
import org.ihtsdo.otf.authoringtemplate.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;

@RestController
@SuppressWarnings("unused")
public class TemplateController {

	@Autowired
	private TemplateService templateService;

	@ResponseBody
	@RequestMapping(value = "/templates", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createTemplate(@RequestParam String name, @RequestBody ConceptTemplate conceptTemplate) throws IOException {
		templateService.create(name, conceptTemplate);
		return ControllerHelper.getCreatedResponse(name);
	}

	@ResponseBody
	@RequestMapping(value = "/templates/{name}", method = RequestMethod.PUT, produces = "application/json")
	public ConceptTemplate updateTemplate(@PathVariable String name, @RequestBody ConceptTemplate conceptTemplate) throws IOException {
		return templateService.update(name, conceptTemplate);
	}

	@ResponseBody
	@RequestMapping(value = "/templates", method = RequestMethod.GET, produces = "application/json")
	public Set<ConceptTemplate> listTemplates() throws IOException {
		return templateService.listAll();
	}

	@ResponseBody
	@RequestMapping(value = "/{branchPath}/templates", method = RequestMethod.GET, produces = "application/json")
	public Set<ConceptTemplate> listTemplates(@PathVariable String branchPath,
											  @RequestParam(required = false) String descendantOf,
											  @RequestParam(required = false) String ancestorOf) throws IOException {
		return templateService.listAll(BranchPathUriUtil.parseBranchPath(branchPath),
				descendantOf, ancestorOf);
	}

	@ResponseBody
	@RequestMapping(value = "/templates/{name}", method = RequestMethod.GET, produces = "application/json")
	public ConceptTemplate getTemplate(@PathVariable String name) throws IOException {
		return templateService.load(name);
	}

}
