package org.ihtsdo.otf.authoringtemplate.rest;

import org.apache.commons.io.FileUtils;
import org.ihtsdo.otf.authoringtemplate.App;
import org.snomed.authoringtemplate.domain.*;
import org.ihtsdo.otf.authoringtemplate.service.JsonStore;
import org.ihtsdo.otf.authoringtemplate.service.TemplateService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@WebAppConfiguration
public class TemplateControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private TemplateService templateService;

	@Autowired
	private JsonStore jsonStore;

	private MockMvc mockMvc;

	@Before
	public void setup() throws IOException {
		mockMvc = webAppContextSetup(webApplicationContext).build();
		ConceptTemplate conceptTemplate = new ConceptTemplate();
		conceptTemplate.setLogicalTemplate("71388002 |Procedure|: 405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure|) @slotX]]\n");
		conceptTemplate.setConceptOutline(new ConceptOutline());
		templateService.create("a%2Fb", conceptTemplate);
	}

	@Test
	public void getTemplate() throws Exception {
		mockMvc.perform(get("/templates/a%2Fb"))
				.andExpect(status().isOk());
	}

	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(jsonStore.getStoreDirectory());
	}

}
