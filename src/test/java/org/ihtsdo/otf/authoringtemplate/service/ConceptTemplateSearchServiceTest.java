package org.ihtsdo.otf.authoringtemplate.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.ihtsdo.otf.authoringtemplate.Config;
import org.ihtsdo.otf.authoringtemplate.TestConfig;
import org.ihtsdo.otf.authoringtemplate.domain.ConceptTemplate;
import org.ihtsdo.otf.authoringtemplate.domain.logical.Attribute;
import org.ihtsdo.otf.authoringtemplate.domain.logical.AttributeGroup;
import org.ihtsdo.otf.authoringtemplate.domain.logical.LogicalTemplate;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClient;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClientFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, TestConfig.class})
public class ConceptTemplateSearchServiceTest {

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
	
	@Test
	public void searchConceptsByLogicalAndLexical() throws ServiceException, IOException, URISyntaxException {
		String templateName = "/templates/CT guided [procedure] of [body structure].json";
		FileUtils.copyFileToDirectory(new File(getClass().getResource(templateName).toURI()), jsonStore.getStoreDirectory());
		ConceptTemplate template = jsonStore.load("CT guided [procedure] of [body structure]", ConceptTemplate.class);
		when(templateService.load(anyString()))
		.thenReturn(template);
		expectGetTerminologyServerClient();
		
		Set<String> concepts = searchService.searchConceptsByTemplate(templateName, "main", false, true);
		assertNotNull(concepts);
	}
	
	
	
	@Test
	public void searchConceptsByLogicalOnly() throws ServiceException, IOException, URISyntaxException {
		String templateName = "/templates/CT guided [procedure] of [body structure].json";
		FileUtils.copyFileToDirectory(new File(getClass().getResource(templateName).toURI()), jsonStore.getStoreDirectory());
		ConceptTemplate template = jsonStore.load("CT guided [procedure] of [body structure]", ConceptTemplate.class);
		when(templateService.load(anyString()))
		.thenReturn(template);
		expectGetTerminologyServerClient();
		
		Set<String> concepts = searchService.searchConceptsByTemplate(templateName, "main", true, true);
		assertNotNull(concepts);
	}
	
	
	private OngoingStubbing<SnowOwlRestClient> expectGetTerminologyServerClient() {
		return when(clientFactory.getClient()).thenReturn(terminologyServerClient);
	}
		
	@Test
	public void testConstructEclQuery() throws IOException {
		LogicalTemplateParserService logicalTemplateParser = new LogicalTemplateParserService();
		String logical = "420134006 |Propensity to adverse reactions (disorder)|:\n" + 
				"	\n" + 
				"	370135005 |Pathological process (attribute)| = 472964009 |Allergic process (qualifier value)|,\n" + 
				"	{\n" + 
				"		246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]],\n" + 
				"		255234002 |After (attribute)| = 609327009 |Allergic sensitization (disorder)|\n" + 
				"	}";
		LogicalTemplate logicalTemplate = logicalTemplateParser.parseTemplate(logical);
		List<String> focusConcepts = logicalTemplate.getFocusConcepts();
		List<AttributeGroup> attributeGroups = logicalTemplate.getAttributeGroups();
		List<Attribute> unGroupedAttriburtes = logicalTemplate.getUngroupedAttributes();
		String ecl = searchService.constructEclQuery(focusConcepts, attributeGroups, unGroupedAttriburtes);
		String expected = "<<420134006:370135005=472964009,{246075003=<105590001 |Substance (substance)|,255234002=609327009}";
		assertEquals(expected, ecl);
	}
	
	
	@Test
	public void testConstructEclQueryWithCardinality() throws IOException {
		LogicalTemplateParserService logicalTemplateParser = new LogicalTemplateParserService();
		String logical = "71388002 |Procedure|:\n\t[[~1..1]] {\n\t\t260686004 |Method| = 312251004 |Computed tomography imaging action|,\n\t\t[[~1..1]] "
				+ "405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure|) @procSite]]\n\t}\n";
		LogicalTemplate logicalTemplate = logicalTemplateParser.parseTemplate(logical);
		List<String> focusConcepts = logicalTemplate.getFocusConcepts();
		List<AttributeGroup> attributeGroups = logicalTemplate.getAttributeGroups();
		List<Attribute> unGroupedAttriburtes = logicalTemplate.getUngroupedAttributes();
		String ecl = searchService.constructEclQuery(focusConcepts, attributeGroups, unGroupedAttriburtes);
		String expected = "<<71388002:[1..1]{260686004=312251004,[1..1]405813007=<< 442083009 |Anatomical or acquired body structure|}";
		assertEquals(expected, ecl);
	}
	
	
	@Test
	public void testConstructEclQueryWithSlotReference() throws IOException {
		LogicalTemplateParserService logicalTemplateParser = new LogicalTemplateParserService();
		String logical = "71388002 |Procedure|:   [[~1..1]] {      260686004 |Method| = 312251004 |Computed tomography imaging action|, "
				+ "     [[~1..1]] 405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure|) @procSite]],"
				+ "      363703001 |Has intent| = 429892002 |Guidance intent|   },   "
				+ "{      260686004 |Method| = [[+id (<< 129264002 |Action|) @action]],      "
				+ "[[~1..1]] 405813007 |Procedure site - Direct| = [[+id $procSite]]   }";
				 
		LogicalTemplate logicalTemplate = logicalTemplateParser.parseTemplate(logical);
		List<String> focusConcepts = logicalTemplate.getFocusConcepts();
		List<AttributeGroup> attributeGroups = logicalTemplate.getAttributeGroups();
		List<Attribute> unGroupedAttriburtes = logicalTemplate.getUngroupedAttributes();
		String ecl = searchService.constructEclQuery(focusConcepts, attributeGroups, unGroupedAttriburtes);
		System.out.println(ecl);
		String expected = "<<71388002:[1..1]{260686004=312251004,[1..1]405813007=<< 442083009 |Anatomical or acquired body structure|,363703001=429892002},"
				+ "{260686004=<< 129264002 |Action|,[1..1]405813007=<< 442083009 |Anatomical or acquired body structure|}";
		assertEquals(expected, ecl);
		
	}
}
	
	
