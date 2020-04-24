package org.ihtsdo.otf.transformationandtemplate.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.snomed.authoringtemplate.domain.logical.LogicalTemplate;
import org.snomed.authoringtemplate.service.LogicalTemplateParserService;

@RunWith(JUnit4.class)
public class TemplateParserServiceTest {
	
	private LogicalTemplateParserService parserService;
	
	@Before
	public void setUp() {
		parserService = new LogicalTemplateParserService();
	}
	
	@Test
	public void testParseLogicTemplate() throws Exception {
		String logicalV2  = "40275004 |Contact dermatitis (disorder)|:\n\t[[~0..1]] 263502005 |Clinical course (attribute)| = [[+id(<288524001 |Courses (qualifier value)|) @clinicalCourse]],\n\t[[~0..1]] 246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]]\n\t[[~1..*]] {\n\t\t[[~1..1]] 116676008 |Associated morphology (attribute)| = 23583003 |Inflammation (morphologic abnormality)|,\n\t\t[[~1..1]] 370135005 |Pathological process (attribute)| = [[+id(<<472963003 |Hypersensitivity process (qualifier value)|) @pathologicalProcess]],\n\t\t[[~1..1]] 363698007 |Finding site (attribute)| = [[+id(<<442083009 |Anatomical or acquired body structure (body structure)|) @bodyStructure]]\n\t}\n";
		LogicalTemplate logicalTemplate = parserService.parseTemplate(logicalV2);
		assertNotNull(logicalTemplate.getFocusConcepts());
		assertEquals(1, logicalTemplate.getFocusConcepts().size());
		assertEquals("40275004", logicalTemplate.getFocusConcepts().iterator().next());
		assertEquals(1, logicalTemplate.getAttributeGroups().size());
	}
}
