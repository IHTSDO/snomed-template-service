package org.ihtsdo.otf.authoringtemplate.service;

import org.snomed.authoringtemplate.domain.logical.LogicalTemplate;
import org.snomed.authoringtemplate.service.LogicalTemplateParserService;

public class TemplateParserTestHarness {
	public static void main(String[] args) throws Exception {
		LogicalTemplateParserService parser = new LogicalTemplateParserService();
		String logical = "281647001 |Adverse reaction (disorder)|:\n\t370135005 |Pathological process (attribute)| = 472964009 |Allergic process (qualifier value)|,\n\t246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]]\n";
		parser.parseTemplate(logical);
		
		String logicalV2 = "281647001 |Adverse reaction (disorder)|:\n [[~1..1]] { \n\t370135005 |Pathological process (attribute)| = 472964009 |Allergic process (qualifier value)|,\n\t246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]]}\n";
		LogicalTemplate logicalTemplate = parser.parseTemplate(logicalV2);
		System.out.println("RelGroups:" + logicalTemplate.getAttributeGroups().size());
	}
		
}
