package org.ihtsdo.otf.transformationandtemplate.service.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.ihtsdo.otf.rest.client.RestClientException;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowstormRestClient;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.exception.ResourceNotFoundException;
import org.ihtsdo.otf.transformationandtemplate.rest.error.InputError;
import org.ihtsdo.otf.transformationandtemplate.service.AbstractServiceTest;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.Relationship;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.*;

import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.CaseSignificance.CASE_INSENSITIVE;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Type.FSN;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class TemplateConceptCreateServiceTest extends AbstractServiceTest {

	@Autowired
	private TemplateConceptCreateService conceptCreateService;

	@Test
	public void testGenerateConcepts_templateNotFound() throws IOException, ServiceException {
		try {
			conceptCreateService.generateConcepts("MAIN/test", "CT Guided Procedure of X", getClass().getResourceAsStream("empty.txt"));
			fail("Should have thrown exception.");
		} catch (ResourceNotFoundException e) {
			assertEquals("template with key CT Guided Procedure of X is not accessible.", e.getMessage());
		}
	}

	@Test
	public void testGenerateConcepts_emptyFile() throws IOException, ServiceException {
		try {
			createCtGuidedProcedureOfX();
			conceptCreateService.generateConcepts("MAIN/test", "CT Guided Procedure of X", getClass().getResourceAsStream("empty.txt"));
			fail("Should have thrown exception.");
		} catch (IllegalArgumentException e) {
			assertEquals("Input file is empty.", e.getMessage());
		}
	}

	@Test
	public void testGenerateConcepts_notEnoughColumns() throws IOException, ServiceException {
		try {
			createCtGuidedProcedureOfX();
			conceptCreateService.generateConcepts("MAIN/test", "CT Guided Procedure of X", getClass().getResourceAsStream("1-col.txt"));
			fail("Should have thrown exception.");
		} catch (IllegalArgumentException e) {
			assertEquals("There are 2 slots requiring input in the selected template is but the header line of the input file has 1 columns.", e.getMessage());
		}
	}

	@Test
	public void testGenerateConcepts_notEnoughValues() throws IOException, ServiceException {
		try {
			createCtGuidedProcedureOfX();
			conceptCreateService.generateConcepts("MAIN/test", "CT Guided Procedure of X", getClass().getResourceAsStream("2-col-value-missing.txt"));
			fail("Should have thrown exception.");
		} catch (InputError e) {
			assertEquals("Line 2 has 1 columns, expecting 2\n" +
					"Value '123' on line 2 column 1 is not a valid concept identifier.\n" +
					"Line 3 has 1 columns, expecting 2\n" +
					"Value '234' on line 3 column 1 is not a valid concept identifier.", e.getMessage());
		}
	}

	@Test
	public void testGenerateConcepts_badConceptIdFormat() throws IOException, ServiceException {
		try {
			createCtGuidedProcedureOfX();
			conceptCreateService.generateConcepts("MAIN/test", "CT Guided Procedure of X", getClass().getResourceAsStream("2-col-bad-conceptId.txt"));
			fail("Should have thrown exception.");
		} catch (InputError e) {
			assertEquals("Value '123' on line 2 column 1 is not a valid concept identifier.\n" +
					"Value '456' on line 2 column 2 is not a valid concept identifier.", e.getMessage());
		}
	}

	
	@Test
	public void testGenerateConceptsWithOptionalAttribute() throws IOException, ServiceException {
		try {
			createCtGuidedProcedureOfX();
			conceptCreateService.generateConcepts("MAIN/test", "CT Guided Procedure of X", getClass().getResourceAsStream("2-col-with-optional-conceptId.txt"));
			fail("Should have thrown exception.");
		} catch (InputError e) {
			assertEquals("Value '123' on line 2 column 1 is not a valid concept identifier.\n" +
					"Value '234' on line 3 column 1 is not a valid concept identifier.\n" + 
					"Value '345' on line 4 column 1 is not a valid concept identifier.", e.getMessage());
		}
	}
	
	@Test
	public void testGenerateConcepts_conceptNotWithinRange() throws IOException, ServiceException {
		try {
			createCtGuidedProcedureOfX();
			mockEclQueryResponse(Collections.singleton("12656001"));
			conceptCreateService.generateConcepts("MAIN/test", "CT Guided Procedure of X", getClass().getResourceAsStream("batch-ct-of-x-error-outside-of-range.txt"));
			fail("Should have thrown exception.");
		} catch (InputError e) {
			assertEquals("Column 1 has the constraint << 442083009 |Anatomical or acquired body structure|. The following given values do not match this constraint: [138875005]\n" +
					"Column 2 has the constraint << 129264002 |Action|. The following given values do not match this constraint: [419988009, 415186003]", e.getMessage());
		}
	}

	@Test
	public void testGenerateConcepts() throws IOException, ServiceException {
		createCtGuidedProcedureOfX();
		mockEclQueryResponse(
				Sets.newHashSet("12656001", "63303001", "63124001", "63125000", "24626005"),
				Sets.newHashSet("419988009", "415186003", "426865009", "426530000", "426413004"));
		
		mockSearchConceptsResponse();

		List<ConceptOutline> conceptOutlines = conceptCreateService.generateConcepts("MAIN/test", "CT Guided Procedure of X", getClass().getResourceAsStream("2-cols-5-values.txt"));
		assertEquals(5, conceptOutlines.size());

		ConceptOutline concept = conceptOutlines.get(0);
		assertEquals(2, concept.getDescriptions().size());
		List<Relationship> relationships = getRelationships(concept);
		assertEquals(6, relationships.size());
		assertEquals("12656001", relationships.get(2).getTarget().getConceptId());
		assertEquals("419988009", relationships.get(4).getTarget().getConceptId());
		assertEquals("12656001", relationships.get(5).getTarget().getConceptId());

		concept = conceptOutlines.get(1);
		assertEquals(2, concept.getDescriptions().size());
		relationships = getRelationships(concept);
		assertEquals(6, relationships.size());
		assertEquals("63303001", relationships.get(2).getTarget().getConceptId());
		assertEquals("415186003", relationships.get(4).getTarget().getConceptId());
		assertEquals("63303001", relationships.get(5).getTarget().getConceptId());

		concept = conceptOutlines.get(2);
		relationships = getRelationships(concept);
		assertEquals(2, concept.getDescriptions().size());
		assertEquals(6, relationships.size());
		assertEquals("63124001", relationships.get(2).getTarget().getConceptId());
		assertEquals("426865009", relationships.get(4).getTarget().getConceptId());
		assertEquals("63124001", relationships.get(5).getTarget().getConceptId());
	}
	
	@Test
	public void testGenerateLoincConcepts() throws IOException, ServiceException {
		String templateName = "LOINC Template - Quality Observable";

		// Load test resource into template store
		InputStream resourceAsStream = getClass().getResourceAsStream("/templates/" + templateName + ".json");
		assertNotNull(resourceAsStream);
		templateService.create(templateName, new ObjectMapper().readValue(resourceAsStream, ConceptTemplate.class));

		ByteArrayOutputStream emptyInputFile = new ByteArrayOutputStream();
		templateService.writeEmptyInputFile(templateName, emptyInputFile);
		String header = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(emptyInputFile.toByteArray()))).readLine();
		assertEquals("Component(optional)\t" +
						"PropertyType\t" +
						"TimeAspect\t" +
						"DirectSite\t" +
						"InheresIn\t" +
						"ScaleType(optional)\t" +
						"LOINC_FSN\t" +
						"LOINC_Unique_ID",
				header);

		// Generate concepts using template
		String lines =
				header + "\n" + // Header
				"\t118598001\t7389001\t123037004\t123037004\t30766002\tLOINC FSN 1\tID 1\n" + // Line 1
				"123037004\t118598001\t7389001\t123037004\t123037004\t\tLOINC FSN 2\tID 2\n"+ // Line 2
				" \t118598001\t7389001\t123037004\t123037004\t \tLOINC FSN 2\tID 2\n"; // Line 3
		mockEclQueryResponse(
				Sets.newHashSet("123037004"),
				Sets.newHashSet("118598001"),
				Sets.newHashSet("7389001"),
				Sets.newHashSet("123037004"),
				Sets.newHashSet("123037004"),
				Sets.newHashSet("30766002"));
		mockSearchConceptsResponse();
		List<ConceptOutline> generatedConcepts = conceptCreateService.generateConcepts("MAIN", templateName, new ByteArrayInputStream(lines.getBytes()));
		assertEquals(3, generatedConcepts.size());
		ConceptOutline c1 = generatedConcepts.get(0);
		List<Relationship> relationships = getRelationships(c1);

		assertEquals(6, relationships.size());
		int relationship = 0;
		assertEquals(0, relationships.get(relationship++).getGroupId());
		assertEquals(0, relationships.get(relationship++).getGroupId());
		assertEquals(0, relationships.get(relationship++).getGroupId());
		assertEquals(0, relationships.get(relationship++).getGroupId());
		assertEquals(0, relationships.get(relationship++).getGroupId());
		assertEquals(0, relationships.get(relationship++).getGroupId());
		
		assertOptionalAttribute(relationships, "370132008", true);

		assertEquals("LOINC FSN 1 (procedure)", c1.getDescriptions().get(0).getTerm());
		assertEquals("LOINC Unique ID:ID 1", c1.getDescriptions().get(1).getTerm());
		assertEquals("LOINC FSN 1", c1.getDescriptions().get(2).getTerm());

		assertEquals("LOINC FSN 2 (procedure)", generatedConcepts.get(1).getDescriptions().get(0).getTerm());
		assertEquals("LOINC Unique ID:ID 2", generatedConcepts.get(1).getDescriptions().get(1).getTerm());
		assertEquals("LOINC FSN 2", generatedConcepts.get(1).getDescriptions().get(2).getTerm());
		
		//check optional attribute ScaleType(370132008);
		relationships = getRelationships(generatedConcepts.get(1));
		assertOptionalAttribute(relationships, "370132008", false);
	}

	private void assertOptionalAttribute(List<Relationship> relationships, String typeId, boolean mustHave) {
		boolean isFound = false;
		for (Relationship rel : relationships) {
			if (typeId.equals(rel.getType().getConceptId())) {
				isFound = true;
				break;
			}
		}
		if (mustHave && !isFound) {
			Assert.fail("Attribute type of 370132008 should be present but is not");
		}
		if (!mustHave && isFound) {
			Assert.fail("Attribute type of 370132008 should not be present but is");
		}
	}

	private OngoingStubbing<SnowstormRestClient> expectGetTerminologyServerClient() {
		return when(clientFactory.getClient()).thenReturn(terminologyServerClient);
	}
	
	private void mockSearchConceptsResponse() {
		expectGetTerminologyServerClient();
		OngoingStubbing<List<ConceptPojo>> when;
		try {
			when = when(terminologyServerClient.searchConcepts(anyString(), anyList()));
		} catch (RestClientException e) {
			throw new RuntimeException(e);
		}
		Map<String,String> conceptFsnMap = new HashMap<>();
		conceptFsnMap.put("123037004", "Body structure (body structure)");
		conceptFsnMap.put("118598001", "Property of measurement (qualifier value)");
		conceptFsnMap.put("7389001", "Time frame (qualifier value)");
		conceptFsnMap.put("30766002", "Quantitative (qualifier value)");
		conceptFsnMap.put("12656001", "Structure of body of pubis (body structure)");
		conceptFsnMap.put("419988009", "Action of drug administration (qualifier value)");
		conceptFsnMap.put("63303001", "Juxtaglomerular apparatus structure (body structure)");
		conceptFsnMap.put("415186003", "Proximal illumination - action (qualifier value)");
		conceptFsnMap.put("63124001", "Structure of posterolateral branch of circle of Willis (body structure)");
		conceptFsnMap.put("426865009", "3D mode ultrasound (qualifier value)");
		conceptFsnMap.put("63125000", "Structure of dorsum of hand (body structure)");
		conceptFsnMap.put("426530000", "Open reduction - action (qualifier value)");
		conceptFsnMap.put("24626005", "Structure of root of mesentery (body structure)");
		conceptFsnMap.put("426413004", "Closed reduction - action (qualifier value)");
		when.thenReturn(mockConceptPojos(conceptFsnMap));
	}

	private List<ConceptPojo> mockConceptPojos(Map<String, String> conceptFsnMap) {
		List<ConceptPojo> results = new ArrayList<>();
		for (String conceptId : conceptFsnMap.keySet()) {
			ConceptPojo pojo = new ConceptPojo();
			pojo.setActive(true);
			pojo.setConceptId(conceptId);
			DescriptionPojo fsn = new DescriptionPojo();
			fsn.setTerm(conceptFsnMap.get(conceptId));
			fsn.setType(FSN);
			fsn.setCaseSignificance(CASE_INSENSITIVE);
			Set<DescriptionPojo> descriptionSet = new HashSet<>();
			descriptionSet.add(fsn);
			pojo.setDescriptions(descriptionSet);
			results.add(pojo);
		}
		return results;
	}

	private void mockEclQueryResponse(Set<String>... conceptIdResults) {
		expectGetTerminologyServerClient();
		OngoingStubbing<Set<String>> when;
		try {
			when = when(terminologyServerClient.eclQuery(anyString(), anyString(), anyInt()));
		} catch (RestClientException e) {
			throw new RuntimeException(e);
		}
		for (Set<String> conceptIdResult : conceptIdResults) {
			when = when.thenReturn(conceptIdResult);
		}
	}
}
