package org.ihtsdo.otf.authoringtemplate.rest;

import org.ihtsdo.otf.authoringtemplate.domain.ConceptTemplate;
import org.ihtsdo.otf.authoringtemplate.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
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
	public ResponseEntity<Object> updateTemplate(@PathVariable String name, @RequestBody ConceptTemplate conceptTemplate) throws IOException {
		templateService.update(name, conceptTemplate);
		return ControllerHelper.getCreatedResponse(name);
	}

	@ResponseBody
	@RequestMapping(value = "/templates", method = RequestMethod.GET, produces = "application/json")
	public List<ConceptTemplate> listTemplates() {
		return templateService.listAll();
	}

	@ResponseBody
	@RequestMapping(value = "/templates/{name}", method = RequestMethod.GET, produces = "application/json")
	public ConceptTemplate getTemplate(@PathVariable String name) throws IOException {
		return templateService.load(name);
	}

}
