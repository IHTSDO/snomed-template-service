package org.ihtsdo.otf.transformationandtemplate.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.ihtsdo.otf.transformationandtemplate.AbstractTest;
import org.ihtsdo.otf.transformationandtemplate.service.JsonStore;
import org.ihtsdo.otf.transformationandtemplate.service.template.TemplateService;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
public class TemplateControllerTest extends AbstractTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private TemplateService templateService;

	@Autowired
	private JsonStore jsonStore;

	private MockMvc mockMvc;

	@Before
	public void setup() throws IOException, ServiceException {
		mockMvc = webAppContextSetup(webApplicationContext).build();
		ConceptTemplate conceptTemplate = new ConceptTemplate();
		conceptTemplate.setLogicalTemplate("71388002 |Procedure|: 405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure|) @slotX]]\n");
		conceptTemplate.setConceptOutline(new ConceptOutline());
		templateService.create("a%2Fb", conceptTemplate);
		templateService.create("Allergy to [substance] (finding)", conceptTemplate);
	}

	@Test
	public void getTemplate() throws Exception {
		mockMvc.perform(get("/templates/a%2Fb"))
				.andExpect(status().isOk());
	}

	
	@Test
	public void getTemplateNameWithBracket() throws Exception {
		mockMvc.perform(get("/templates/Allergy to [substance] (finding)"))
				.andExpect(status().isOk());
	}
	
	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(jsonStore.getStoreDirectory());
	}

}
