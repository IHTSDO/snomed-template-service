package org.ihtsdo.otf.authoringtemplate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.assertj.core.util.Lists;
import org.ihtsdo.otf.authoringtemplate.Config;
import org.ihtsdo.otf.authoringtemplate.TestConfig;
import org.ihtsdo.otf.authoringtemplate.domain.*;
import org.ihtsdo.otf.authoringtemplate.rest.error.InputError;
import org.ihtsdo.otf.authoringtemplate.service.exception.ResourceNotFoundException;
import org.ihtsdo.otf.authoringtemplate.service.termserver.TerminologyServerAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, TestConfig.class})
public class TemplateServiceTest {

	@Autowired
	private TemplateService templateService;

	@Autowired
	private TemplateStore templateStore;

	@MockBean
	private TerminologyServerAdapter terminologyServerAdapter;

	@Test
	public void testCreate() throws Exception {
		final ConceptTemplate templateRequest = new ConceptTemplate();
		templateRequest.setLogicalTemplate("71388002 |Procedure|:\n" +
				"\t[[~1..1]] {\n" +
				"\t\t260686004 |Method| = 312251004 |Computed tomography imaging action|,\n" +
				"\t\t[[~1..1]] 405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure|) @proc]]\n" +
				"\t}\n");

		templateRequest.addLexicalTemplate(new LexicalTemplate("slotX", "Procedure", "proc", Lists.newArrayList("entire")));
		templateRequest.setConceptOutline(new ConceptOutline(DefinitionStatus.FULLY_DEFINED).addDescription(new Description("CT of $slotX$")));
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
				"target=ConceptMini{conceptId='71388002'}, targetSlot=null, cardinalityMin='null', cardinalityMax='null'}",
				rel.get(0).toString());
		assertEquals("Relationship{characteristicType='STATED_RELATIONSHIP', groupId=1, type=ConceptMini{conceptId='405813007'}, " +
				"target=null, targetSlot=SimpleSlot{slotName='proc', " +
				"allowableRangeECL='<< 442083009 |Anatomical or acquired body structure|', slotReference='null'}, cardinalityMin='1', cardinalityMax='1'}",
				rel.get(1).toString());
		assertEquals("Relationship{characteristicType='STATED_RELATIONSHIP', groupId=1, type=ConceptMini{conceptId='363703001'}, " +
				"target=ConceptMini{conceptId='429892002'}, targetSlot=null, cardinalityMin='null', cardinalityMax='null'}",
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
	public void testListAll() throws IOException {
		String focusConcept = "302509004";
		createTemplateWithFocusConcept("one", focusConcept);

		templateService.listAll("MAIN/task", new String[] {"123037004"}, null);
		assertEclExpressionCreated("(302509004) AND (<<123037004)");

		templateService.listAll("MAIN/task", null, new String[]{"123037004"});
		assertEclExpressionCreated("(302509004) AND (>>123037004)");

		templateService.listAll("MAIN/task", new String[] {"123037004"}, new String[] {"123037004"});
		assertEclExpressionCreated("(302509004) AND (<<123037004 OR >>123037004)");

		templateService.listAll("MAIN/task", new String[] {"123037004", "123037004"}, null);
		assertEclExpressionCreated("(302509004) AND (<<123037004 OR <<123037004)");

		templateService.listAll("MAIN/task", new String[] {"123037004", "123037004"}, new String[] {"123037004", "123037004"});
		assertEclExpressionCreated("(302509004) AND (<<123037004 OR <<123037004 OR >>123037004 OR >>123037004)");

	}

	@Test
	public void testWriteEmptyInputFile() throws IOException, ResourceNotFoundException {
		createCtGuidedProcedureOfX();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		templateService.writeEmptyInputFile("", "CT Guided Procedure of X", stream);
		assertEquals("procSite (<< 442083009 |Anatomical or acquired body structure|)\taction (<< 129264002 |Action|)\n", new String(stream.toByteArray()));
	}

	@Test
	public void testGenerateConcepts_templateNotFound() throws IOException, ResourceNotFoundException {
		try {
			templateService.generateConcepts("MAIN/test", "CT Guided Procedure of X", getClass().getResourceAsStream("empty.txt"));
			fail("Should have thrown exception.");
		} catch (ResourceNotFoundException e) {
			assertEquals("Resource of type template and ID 'CT Guided Procedure of X' was not found.", e.getMessage());
		}
	}

	@Test
	public void testGenerateConcepts_emptyFile() throws IOException, ResourceNotFoundException {
		try {
			createCtGuidedProcedureOfX();
			templateService.generateConcepts("MAIN/test", "CT Guided Procedure of X", getClass().getResourceAsStream("empty.txt"));
			fail("Should have thrown exception.");
		} catch (IllegalArgumentException e) {
			assertEquals("Input file is empty.", e.getMessage());
		}
	}

	@Test
	public void testGenerateConcepts_notEnoughColumns() throws IOException, ResourceNotFoundException {
		try {
			createCtGuidedProcedureOfX();
			templateService.generateConcepts("MAIN/test", "CT Guided Procedure of X", getClass().getResourceAsStream("1-col.txt"));
			fail("Should have thrown exception.");
		} catch (IllegalArgumentException e) {
			assertEquals("There are 2 slots requiring input in the selected template is but the header line of the input file has 1 columns.", e.getMessage());
		}
	}

	@Test
	public void testGenerateConcepts_notEnoughValues() throws IOException, ResourceNotFoundException {
		try {
			createCtGuidedProcedureOfX();
			templateService.generateConcepts("MAIN/test", "CT Guided Procedure of X", getClass().getResourceAsStream("2-col-value-missing.txt"));
			fail("Should have thrown exception.");
		} catch (InputError e) {
			assertEquals("Line 2 has 1 columns, expecting 2\n" +
					"Value '123' on line 2 column 1 is not a valid concept identifier.\n" +
					"Line 3 has 1 columns, expecting 2\n" +
					"Value '234' on line 3 column 1 is not a valid concept identifier.", e.getMessage());
		}
	}

	@Test
	public void testGenerateConcepts_badConceptIdFormat() throws IOException, ResourceNotFoundException {
		try {
			createCtGuidedProcedureOfX();
			templateService.generateConcepts("MAIN/test", "CT Guided Procedure of X", getClass().getResourceAsStream("2-col-bad-conceptId.txt"));
			fail("Should have thrown exception.");
		} catch (InputError e) {
			assertEquals("Value '123' on line 2 column 1 is not a valid concept identifier.\n" +
					"Value '456' on line 2 column 2 is not a valid concept identifier.", e.getMessage());
		}
	}

	@Test
	public void testGenerateConcepts_conceptNotWithinRange() throws IOException, ResourceNotFoundException {
		try {
			createCtGuidedProcedureOfX();
			mockEclQueryResponse(Collections.singleton("12656001"));
			templateService.generateConcepts("MAIN/test", "CT Guided Procedure of X", getClass().getResourceAsStream("batch-ct-of-x-error-outside-of-range.txt"));
			fail("Should have thrown exception.");
		} catch (InputError e) {
			assertEquals("Column 1 has the constraint << 442083009 |Anatomical or acquired body structure|. The following given values do not match this constraint: [138875005]\n" +
					"Column 2 has the constraint << 129264002 |Action|. The following given values do not match this constraint: [419988009, 415186003]", e.getMessage());
		}
	}

	@Test
	public void testGenerateConcepts() throws IOException, ResourceNotFoundException {
		createCtGuidedProcedureOfX();
		mockEclQueryResponse(
				Sets.newHashSet("12656001", "63303001", "63124001", "63125000", "24626005"),
				Sets.newHashSet("419988009", "415186003", "426865009", "426530000", "426413004"));

		List<ConceptOutline> conceptOutlines = templateService.generateConcepts("MAIN/test", "CT Guided Procedure of X", getClass().getResourceAsStream("2-cols-5-values.txt"));
		assertEquals(5, conceptOutlines.size());

		ConceptOutline concept = conceptOutlines.get(0);
		assertEquals(1, concept.getDescriptions().size());
		assertEquals(6, concept.getRelationships().size());
		assertEquals("12656001", concept.getRelationships().get(2).getTarget().getConceptId());
		assertEquals("419988009", concept.getRelationships().get(4).getTarget().getConceptId());
		assertEquals("12656001", concept.getRelationships().get(5).getTarget().getConceptId());

		concept = conceptOutlines.get(1);
		assertEquals(1, concept.getDescriptions().size());
		assertEquals(6, concept.getRelationships().size());
		assertEquals("63303001", concept.getRelationships().get(2).getTarget().getConceptId());
		assertEquals("415186003", concept.getRelationships().get(4).getTarget().getConceptId());
		assertEquals("63303001", concept.getRelationships().get(5).getTarget().getConceptId());

		concept = conceptOutlines.get(2);
		assertEquals(1, concept.getDescriptions().size());
		assertEquals(6, concept.getRelationships().size());
		assertEquals("63124001", concept.getRelationships().get(2).getTarget().getConceptId());
		assertEquals("426865009", concept.getRelationships().get(4).getTarget().getConceptId());
		assertEquals("63124001", concept.getRelationships().get(5).getTarget().getConceptId());
	}

	@Test
	public void testGenerateLoincConcepts() throws IOException, ResourceNotFoundException {
		String templateName = "LOINC Template - Quality Observable";

		// Load test resource into template store
		InputStream resourceAsStream = getClass().getResourceAsStream("/templates/" + templateName + ".json");
		assertNotNull(resourceAsStream);
		templateService.create(templateName, new ObjectMapper().readValue(resourceAsStream, ConceptTemplate.class));

		ByteArrayOutputStream emptyInputFile = new ByteArrayOutputStream();
		templateService.writeEmptyInputFile("MAIN", templateName, emptyInputFile);
		String header = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(emptyInputFile.toByteArray()))).readLine();
		assertEquals("Component (<<123037004 |Body structure| OR <<410607006 |Organism| OR <<105590001 |Substance| OR <<123038009 |Specimen| OR <<260787004 |Physical object| OR <<373873005 |Pharmaceutical / biologic product| OR <<419891008 |Record artifact|)\t" +
						"PropertyType (<<118598001 |Measurement property|)\t" +
						"TimeAspect (<7389001 |Time frame|)\t" +
						"DirectSite (<<123037004 |Body structure| OR <<410607006 |Organism| OR <<105590001 |Substance| OR <<123038009 |Specimen| OR <<260787004 |Physical object| OR <<373873005 |Pharmaceutical / biologic product| OR <<419891008 |Record artifact|)\t" +
						"InheresIn (<<123037004 |Body structure| OR <<410607006 |Organism| OR <<105590001 |Substance| OR <<123038009 |Specimen| OR <<260787004 |Physical object| OR <<373873005 |Pharmaceutical / biologic product| OR <<419891008 |Record artifact|)\t" +
						"ScaleType (<<30766002 |Quantitative| OR <<26716007 |Qualitative| OR  <<117363000 |Ordinal value| OR <<117365007 |Ordinal or quantitative value| OR <<117362005 |Nominal value| OR <<117364006 |Narrative value| OR <<117444000 |Text value|)\t" +
						"LOINC_FSN\t" +
						"LOINC_Unique_ID",
				header);

		// Generate concepts using template
		String lines =
				header + "\n" + // Header
				"123037004\t118598001\t7389001\t123037004\t123037004\t30766002\tLOINC FSN 1\tID 1\n" + // Line 1
				"123037004\t118598001\t7389001\t123037004\t123037004\t30766002\tLOINC FSN 2\tID 2\n"; // Line 2
		mockEclQueryResponse(
				Sets.newHashSet("123037004"),
				Sets.newHashSet("118598001"),
				Sets.newHashSet("7389001"),
				Sets.newHashSet("123037004"),
				Sets.newHashSet("123037004"),
				Sets.newHashSet("30766002"));
		List<ConceptOutline> generatedConcepts = templateService.generateConcepts("MAIN", templateName, new ByteArrayInputStream(lines.getBytes()));
		assertEquals(2, generatedConcepts.size());
		assertEquals("LOINC FSN 1 (procedure)", generatedConcepts.get(0).getDescriptions().get(0).getTerm());
		assertEquals("LOINC FSN 1", generatedConcepts.get(0).getDescriptions().get(1).getTerm());
		assertEquals("LOINC Unique ID:ID 1", generatedConcepts.get(0).getDescriptions().get(2).getTerm());
		assertEquals("LOINC FSN 2 (procedure)", generatedConcepts.get(1).getDescriptions().get(0).getTerm());
		assertEquals("LOINC FSN 2", generatedConcepts.get(1).getDescriptions().get(1).getTerm());
		assertEquals("LOINC Unique ID:ID 2", generatedConcepts.get(1).getDescriptions().get(2).getTerm());
	}

	private void createCtGuidedProcedureOfX() throws IOException {
		final ConceptTemplate templateRequest = new ConceptTemplate();
		templateRequest.setLogicalTemplate("71388002 |Procedure|:   [[~1..1]] {      260686004 |Method| = 312251004 |Computed tomography imaging action|,      [[~1..1]] 405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure|) @procSite]],      363703001 |Has intent| = 429892002 |Guidance intent|   },   {      260686004 |Method| = [[+id (<< 129264002 |Action|) @action]],      [[~1..1]] 405813007 |Procedure site - Direct| = [[+id $procSite]]   }");

		templateRequest.addLexicalTemplate(new LexicalTemplate("procSiteTerm", "X", "procSite", Lists.newArrayList("structure of", "structure", "part of")));
		templateRequest.addLexicalTemplate(new LexicalTemplate("actionTerm", "Procedure", "action", Lists.newArrayList(" - action")));
		templateRequest.setConceptOutline(new ConceptOutline().addDescription(new Description("$actionTerm$ of $procSiteTerm$ using computed tomography guidance (procedure)")));
		templateService.create("CT Guided Procedure of X", templateRequest);
	}

	private void assertEclExpressionCreated(String expected) {
		ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
		verify(terminologyServerAdapter).eclQueryHasAnyMatches(anyString(), stringArgumentCaptor.capture());
		assertEquals(expected, stringArgumentCaptor.getValue());
		reset(terminologyServerAdapter);
	}

	private void mockEclQueryResponse(Set<String>... conceptIdResults) {
		OngoingStubbing<Set<String>> when = when(terminologyServerAdapter.eclQuery(anyString(), anyString(), anyInt()));
		for (Set<String> conceptIdResult : conceptIdResults) {
			when = when.thenReturn(conceptIdResult);
		}
	}

	private void createTemplateWithFocusConcept(String name, String focusConcept) throws IOException {
		ConceptTemplate template = new ConceptTemplate();
		template.setFocusConcept(focusConcept);
		template.setLogicalTemplate(focusConcept);
		template.setConceptOutline(new ConceptOutline());
		templateService.create(name, template);
	}

	@Before
	public void before() {
		templateStore.clear();
	}

	@After
	public void after() {
		// Recreate empty template store
		FileSystemUtils.deleteRecursively(templateStore.getJsonStore().getStoreDirectory());
		templateStore.getJsonStore().getStoreDirectory().mkdirs();
	}

}
