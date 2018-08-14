package org.ihtsdo.otf.authoringtemplate.service;

import org.snomed.authoringtemplate.domain.logical.LogicalTemplate;
import org.snomed.authoringtemplate.service.LogicalTemplateParserService;

public class TemplateParserTestHarness {
	public static void main(String[] args) throws Exception {
		LogicalTemplateParserService parser = new LogicalTemplateParserService();
		String logicalV2  = "40275004 |Contact dermatitis (disorder)|:\n\t[[~0..1]] [[+id(< 263502005 |Clinical course (attribute)|) @clinicalCourseSubType]] = [[+id(<288524001 |Courses (qualifier value)|) @clinicalCourse]],\n\t[[~0..1]] 246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]]\n\t[[~1..*]] {\n\t\t[[1..1]] 116676008 |Associated morphology (attribute)| = 23583003 |Inflammation (morphologic abnormality)|,\n\t\t[[1..1]] 370135005 |Pathological process (attribute)| = [[+id(<<472963003 |Hypersensitivity process (qualifier value)|) @pathologicalProcess]],\n\t\t[[1..1]] 363698007 |Finding site (attribute)| = [[+id(<<442083009 |Anatomical or acquired body structure (body structure)|) @bodyStructure]]\n\t}\n";
		LogicalTemplate logicalTemplate = parser.parseTemplate(logicalV2);
		System.out.println("RelGroups:" + logicalTemplate.getAttributeGroups().size());
	}
}
