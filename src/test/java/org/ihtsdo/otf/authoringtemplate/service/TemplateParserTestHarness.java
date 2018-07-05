package org.ihtsdo.otf.authoringtemplate.service;

import java.io.File;
import java.io.FileInputStream;

import org.snomed.authoringtemplate.service.LogicalTemplateParserService;

public class TemplateParserTestHarness {
	private static String rootDir = "/Users/mchu/Development/snomed-template-service/snomed-templates/";
	public static void main(String[] args) throws Exception {
		LogicalTemplateParserService parser = new LogicalTemplateParserService();
//		String logical = "281647001 |Adverse reaction (disorder)|:\n\t370135005 |Pathological process (attribute)| = 472964009 |Allergic process (qualifier value)|,\n\t246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]]\n";
//		parser.parseTemplate(logical);
//				
		String logicalTemplate = rootDir + "Allergic_ReactionV1.txt";
		if (args != null && args.length > 0) {
			logicalTemplate = args[0];
		}
		parser.parseTemplate(new FileInputStream(new File(logicalTemplate)));
	}
	
	

}
