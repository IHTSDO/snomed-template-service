package org.ihtsdo.otf.authoringtemplate.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.assertj.core.util.Lists;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.RestClientException;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClient;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.OngoingStubbing;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.Concepts;
import org.snomed.authoringtemplate.domain.DefinitionStatus;
import org.snomed.authoringtemplate.domain.Description;
import org.snomed.authoringtemplate.domain.LexicalTemplate;
import org.snomed.authoringtemplate.domain.Relationship;

public class TemplateServiceTest extends AbstractServiceTest {

	@Test
	public void testCreate() throws Exception {
		final ConceptTemplate templateRequest = new ConceptTemplate();
		templateRequest.setLogicalTemplate("71388002 |Procedure|:\n" +
				"\t[[~1..1]] {\n" +
				"\t\t260686004 |Method| = 312251004 |Computed tomography imaging action|,\n" +
				"\t\t[[~1..1]] 405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure|) @proc]]\n" +
				"\t}\n");

		templateRequest.addLexicalTemplate(new LexicalTemplate("slotX", "Procedure", "proc", Lists.newArrayList("entire")));
		templateRequest.setConceptOutline(new ConceptOutline(DefinitionStatus.FULLY_DEFINED).addDescription(new Description("CT of $slotX$")).setModuleId(org.ihtsdo.otf.constants.Concepts.MODULE));
		final String name = templateService.create("one", templateRequest);

		final ConceptTemplate template = templateService.load(name);
		assertEquals("one", template.getName());
		assertEquals("71388002", template.getFocusConcept());
		assertEquals(1, template.getVersion());

		final List<LexicalTemplate> lexicalTemplates = template.getLexicalTemplates();
		assertEquals(1, lexicalTemplates.size());
		assertEquals("slotX", lexicalTemplates.get(0).getName());
		assertEquals("proc", lexicalTemplates.get(0).getTakeFSNFromSlot());
		assertEquals("[entire]", lexicalTemplates.get(0).getRemoveParts().toString());

		final ConceptOutline conceptOutline = template.getConceptOutline();

		assertEquals(DefinitionStatus.FULLY_DEFINED, conceptOutline.getDefinitionStatus());
		assertEquals(org.ihtsdo.otf.constants.Concepts.MODULE, conceptOutline.getModuleId());

		final List<Relationship> relationships = conceptOutline.getRelationships();
		assertEquals(3, relationships.size());
		final Relationship relationship = relationships.get(0);
		assertEquals(Concepts.ISA, relationship.getType().getConceptId());

		assertEquals(1, conceptOutline.getDescriptions().size());
		final Description actualDescription = conceptOutline.getDescriptions().get(0);
		assertEquals("CT of $slotX$", actualDescription.getTermTemplate());
		assertEquals("CT of [Procedure]", actualDescription.getInitialTerm());
	}

	@Test
	public void testUpdate() throws Exception {
		final ConceptTemplate templateRequest1 = new ConceptTemplate();
		templateRequest1.setLogicalTemplate("71388002 |Procedure|:\n" +
				"\t[[~1..1]] {\n" +
				"\t\t260686004 |Method| = 312251004 |Computed tomography imaging action|,\n" +
				"\t\t[[~1..1]] 405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure|) @proc]]\n" +
				"\t}\n");

		templateRequest1.addLexicalTemplate(new LexicalTemplate("slotX", "Procedure", "proc", Lists.newArrayList("entire")));
		templateRequest1.setConceptOutline(new ConceptOutline().addDescription(new Description("CT of $slotX$")));
		final String name = templateService.create("one", templateRequest1);

		final ConceptTemplate templateRequest2 = new ConceptTemplate();
		templateRequest2.setLogicalTemplate("71388002 |Procedure|:\n" +
				"\t[[~1..1]] {\n" +
				"\t\t[[~1..1]] 405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure|) @proc]],\n" +
				"\t\t363703001 |Has intent| = 429892002 |Guidance intent|\n" +
				"\t}\n");

		templateRequest2.addLexicalTemplate(new LexicalTemplate("proc", "Procedure", "proc", Lists.newArrayList("entire")));
		templateRequest2.setConceptOutline(new ConceptOutline().addDescription(new Description("CT of $proc$")));

		final ConceptTemplate updated = templateService.update("one", templateRequest2);

		final List<Relationship> rel = updated.getConceptOutline().getRelationships();
		assertEquals(3, rel.size());
		assertEquals("Relationship{characteristicType='STATED_RELATIONSHIP', groupId=0, type=ConceptMini{conceptId='116680003'}, " +
				"target=ConceptMini{conceptId='71388002'}, targetSlot=null, cardinalityMin='1', cardinalityMax='*'}",
				rel.get(0).toString());
		assertEquals("Relationship{characteristicType='STATED_RELATIONSHIP', groupId=1, type=ConceptMini{conceptId='405813007'}, " +
				"target=null, targetSlot=SimpleSlot{slotName='proc', " +
				"allowableRangeECL='<< 442083009 |Anatomical or acquired body structure|', slotReference='null'}, cardinalityMin='1', cardinalityMax='1'}",
				rel.get(1).toString());
		assertEquals("Relationship{characteristicType='STATED_RELATIONSHIP', groupId=1, type=ConceptMini{conceptId='363703001'}, " +
				"target=ConceptMini{conceptId='429892002'}, targetSlot=null, cardinalityMin='1', cardinalityMax='*'}",
				rel.get(2).toString());

		final List<Description> desc = updated.getConceptOutline().getDescriptions();
		assertEquals(1, desc.size());
		assertEquals("CT of $proc$", desc.get(0).getTermTemplate());
		assertEquals("CT of [Procedure]", desc.get(0).getInitialTerm());

		final List<LexicalTemplate> lex = updated.getLexicalTemplates();
		assertEquals(1, lex.size());
		assertEquals("proc", lex.get(0).getName());
	}

	@Test
	public void testListAll() throws Exception {
		String focusConcept = "302509004";
		createTemplateWithFocusConcept("one", focusConcept);
		expectGetTerminologyServerClient();

		templateService.listAll("MAIN/task", new String[] {"123037004"}, null);
		assertEclExpressionCreated("(302509004 AND <<123037004)");

		templateService.listAll("MAIN/task", null, new String[]{"123037004"});
		assertEclExpressionCreated("(302509004 AND >>123037004)");

		templateService.listAll("MAIN/task", new String[] {"123037004"}, new String[] {"123037004"});
		assertEclExpressionCreated("(302509004 AND <<123037004) OR (302509004 AND >>123037004)");

		templateService.listAll("MAIN/task", new String[] {"123037004", "123037004"}, null);
		assertEclExpressionCreated("(302509004 AND <<123037004) OR (302509004 AND <<123037004)");

		templateService.listAll("MAIN/task", new String[] {"123037004", "123037004"}, new String[] {"123037004", "123037004"});
		assertEclExpressionCreated("(302509004 AND <<123037004) OR (302509004 AND <<123037004) OR (302509004 AND >>123037004) OR (302509004 AND >>123037004)");
	}

	@Test
	public void testWriteEmptyInputFile() throws Exception {
		createCtGuidedProcedureOfX();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		templateService.writeEmptyInputFile("", "CT Guided Procedure of X", stream);
		assertEquals("procSite\taction\n", new String(stream.toByteArray()));
	}
	
	private OngoingStubbing<SnowOwlRestClient> expectGetTerminologyServerClient() {
		return when(clientFactory.getClient()).thenReturn(terminologyServerClient);
	}

	private void assertEclExpressionCreated(String expected) {
		expectGetTerminologyServerClient();
		ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
		try {
			verify(terminologyServerClient).eclQueryHasAnyMatches(anyString(), stringArgumentCaptor.capture());
		} catch (RestClientException e) {
			throw new RuntimeException(e);
		}
		assertEquals(expected, stringArgumentCaptor.getValue());
		reset(terminologyServerClient);
	}

	private void createTemplateWithFocusConcept(String name, String focusConcept) throws IOException, ServiceException {
		ConceptTemplate template = new ConceptTemplate();
		template.setFocusConcept(focusConcept);
		template.setLogicalTemplate(focusConcept);
		template.setConceptOutline(new ConceptOutline());
		templateService.create(name, template);
	}
}
