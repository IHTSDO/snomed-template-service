package org.ihtsdo.otf.authoringtemplate.service;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.ihtsdo.otf.authoringtemplate.Config;
import org.ihtsdo.otf.authoringtemplate.TestConfig;
import org.ihtsdo.otf.authoringtemplate.domain.ConceptTemplate;
import org.ihtsdo.otf.authoringtemplate.domain.logical.Attribute;
import org.ihtsdo.otf.authoringtemplate.domain.logical.AttributeGroup;
import org.ihtsdo.otf.authoringtemplate.domain.logical.LogicalTemplate;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClient;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClientFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, TestConfig.class})
public class ConceptTempalteSearchTestHarness {

	@Autowired
	private ConceptTemplateSearchService searchService;
	
	@MockBean
	private TemplateService templateService;
	
	
	@MockBean
	private SnowOwlRestClientFactory clientFactory;

	@MockBean
	private SnowOwlRestClient terminologyServerClient;
	
	@Autowired
	private JsonStore jsonStore;
	
	private LogicalTemplateParserService logicalTemplateParser;
	
	@Before
	public void setUp() {
		logicalTemplateParser = new LogicalTemplateParserService();
	}
	
	@Test
	public void constructEcl() throws Exception {
		String templateName = "LOINC Template - Process Observable - 100 - 2";
		String rootPath ="/Users/mchu/Development/snomed-templates/";
		File templateFile = new File(rootPath + templateName + ".json");
		FileUtils.copyFileToDirectory(templateFile, jsonStore.getStoreDirectory());
		ConceptTemplate template = jsonStore.load(templateName, ConceptTemplate.class);
		LogicalTemplate logicalTemplate = logicalTemplateParser.parseTemplate(template.getLogicalTemplate());
		List<String> focusConcepts = logicalTemplate.getFocusConcepts();
		List<AttributeGroup> attributeGroups = logicalTemplate.getAttributeGroups();
		List<Attribute> unGroupedAttriburtes = logicalTemplate.getUngroupedAttributes();
		String ecl = searchService.constructEclQuery(focusConcepts, attributeGroups, unGroupedAttriburtes);
		System.out.println("ECL=" + ecl);
		
	}
}
