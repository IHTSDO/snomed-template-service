package org.ihtsdo.otf.authoringtemplate.service;

import org.assertj.core.util.Lists;
import org.ihtsdo.otf.authoringtemplate.Config;
import org.ihtsdo.otf.authoringtemplate.TestConfig;
import org.ihtsdo.otf.authoringtemplate.domain.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileSystemUtils;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, TestConfig.class})
public class TemplateServiceTest {

	@Autowired
	private TemplateService templateService;

	@Test
	public void testCreate() throws Exception {
		final ConceptTemplate templateRequest = new ConceptTemplate();
		templateRequest.setLogicalTemplate("71388002 |Procedure|:\n" +
				"\t[[~1..1]] {\n" +
				"\t\t260686004 |Method| = 312251004 |Computed tomography imaging action|,\n" +
				"\t\t[[~1..1]] 405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure|) @proc]]\n" +
				"\t}\n");

		templateRequest.addLexicalTemplate(new LexicalTemplate("slotX", "proc", Lists.newArrayList("entire")));
		templateRequest.setConceptOutline(new ConceptOutline().addDescription(new Description("CT of $slotX")));
		final String name = templateService.create("one", templateRequest);

		final ConceptTemplate template = templateService.load(name);
		Assert.assertEquals("one", template.getName());
		Assert.assertEquals("71388002", template.getFocusConcept());
		Assert.assertEquals(1, template.getVersion());

		final List<LexicalTemplate> lexicalTemplates = template.getLexicalTemplates();
		Assert.assertEquals(1, lexicalTemplates.size());
		Assert.assertEquals("slotX", lexicalTemplates.get(0).getName());
		Assert.assertEquals("proc", lexicalTemplates.get(0).getTakeFSNFromSlot());
		Assert.assertEquals("[entire]", lexicalTemplates.get(0).getRemoveParts().toString());

		final ConceptOutline conceptOutline = template.getConceptOutline();
		final List<Relationship> relationships = conceptOutline.getRelationships();
		Assert.assertEquals(3, relationships.size());
		final Relationship relationship = relationships.get(0);
		Assert.assertEquals(Concepts.ISA, relationship.getType().getConceptId());
	}

	@Test
	public void testUpdate() throws Exception {
		final ConceptTemplate templateRequest1 = new ConceptTemplate();
		templateRequest1.setLogicalTemplate("71388002 |Procedure|:\n" +
				"\t[[~1..1]] {\n" +
				"\t\t260686004 |Method| = 312251004 |Computed tomography imaging action|,\n" +
				"\t\t[[~1..1]] 405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure|) @proc]]\n" +
				"\t}\n");

		templateRequest1.addLexicalTemplate(new LexicalTemplate("slotX", "proc", Lists.newArrayList("entire")));
		templateRequest1.setConceptOutline(new ConceptOutline().addDescription(new Description("CT of $slotX")));
		final String name = templateService.create("one", templateRequest1);

		final ConceptTemplate templateRequest2 = new ConceptTemplate();
		templateRequest2.setLogicalTemplate("71388002 |Procedure|:\n" +
				"\t[[~1..1]] {\n" +
				"\t\t[[~1..1]] 405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure|) @proc]],\n" +
				"\t\t363703001 |Has intent| = 429892002 |Guidance intent|\n" +
				"\t}\n");

		templateRequest2.addLexicalTemplate(new LexicalTemplate("proc", "proc", Lists.newArrayList("entire")));
		templateRequest2.setConceptOutline(new ConceptOutline().addDescription(new Description("CT of $proc")));

		final ConceptTemplate updated = templateService.update("one", templateRequest2);

		final List<Relationship> rel = updated.getConceptOutline().getRelationships();
		Assert.assertEquals(3, rel.size());
		Assert.assertEquals("Relationship{characteristicType='STATED_RELATIONSHIP', groupId=0, type=ConceptMini{conceptId='116680003'}, " +
				"target=ConceptMini{conceptId='71388002'}, targetSlot=null, cardinalityMin='null', cardinalityMax='null'}",
				rel.get(0).toString());
		Assert.assertEquals("Relationship{characteristicType='STATED_RELATIONSHIP', groupId=1, type=ConceptMini{conceptId='405813007'}, " +
				"target=null, targetSlot=SimpleSlot{slotName='proc', " +
				"allowableRangeECL='<< 442083009 |Anatomical or acquired body structure|', slotReference='null'}, cardinalityMin='1', cardinalityMax='1'}",
				rel.get(1).toString());
		Assert.assertEquals("Relationship{characteristicType='STATED_RELATIONSHIP', groupId=1, type=ConceptMini{conceptId='363703001'}, " +
				"target=ConceptMini{conceptId='429892002'}, targetSlot=null, cardinalityMin='null', cardinalityMax='null'}",
				rel.get(2).toString());

		final List<Description> desc = updated.getConceptOutline().getDescriptions();
		Assert.assertEquals(1, desc.size());
		Assert.assertEquals("CT of $proc", desc.get(0).getTerm());

		final List<LexicalTemplate> lex = updated.getLexicalTemplates();
		Assert.assertEquals(1, lex.size());
		Assert.assertEquals("proc", lex.get(0).getName());
	}

	@After
	public void after() {
		System.out.println(templateService.getJsonStore().getStoreDirectory().getAbsolutePath());
		FileSystemUtils.deleteRecursively(templateService.getJsonStore().getStoreDirectory());
	}

}
