package org.ihtsdo.otf.transformationandtemplate.service.template;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.ihtsdo.otf.rest.client.RestClientException;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowstormRestClient;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.*;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptMiniPojo.DescriptionMiniPojo;
import org.ihtsdo.otf.transformationandtemplate.service.AbstractServiceTest;
import org.ihtsdo.otf.transformationandtemplate.service.Constants;
import org.ihtsdo.otf.transformationandtemplate.service.JsonStore;
import org.ihtsdo.otf.transformationandtemplate.service.TestDataHelper;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;
import org.skyscreamer.jsonassert.JSONAssert;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo.InactivationIndicator.ERRONEOUS;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Acceptability.ACCEPTABLE;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Acceptability.PREFERRED;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.CaseSignificance.CASE_INSENSITIVE;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Type.FSN;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class TemplateConceptTransformServiceTest extends AbstractServiceTest {

	@Autowired
	private TemplateConceptTransformService transformService;

	@Autowired
	private JsonStore templateJsonStore;
	
	private ConceptPojo conceptToTransform;
	
	private ConceptPojo transformedConcept;
	
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	// set it to true to print out concept in json
	private boolean isDebug = false;
	
	private TemplateTransformRequest transformRequest;
	
	private String source;
	
	private String destination;
	
	@Before
	public void setUp() throws Exception {
		source = "Allergy to [substance]";
		destination = "Allergy to [substance] V2";
		transformRequest = new TemplateTransformRequest(source, destination);
		setUpTemplates(source, destination);
	}
	
	@Test
	public void testCreateTemplateTransformation() throws ServiceException {
		TemplateTransformRequest transformRequest = new TemplateTransformRequest();
		Set<String> concepts = Collections.singleton("123555");
		transformRequest.setConceptsToTransform(concepts);
		TemplateTransformation transformation = transformService.createTemplateTransformation("MAIN", transformRequest);
		assertNotNull(transformation);
	}
	
	@Test
	public void testValidateWithSuccess() {
		try {
			ConceptTemplate sourceTemplate = templateService.loadOrThrow(source);
			ConceptTemplate destinationTemplate = templateService.loadOrThrow(destination);
			transformService.validate(sourceTemplate, destinationTemplate);
		} catch (Exception e) {
			fail("No exception is expected to be thrown." + e.getMessage());
		}
	}
	
	
	@Test(expected=ServiceException.class)
	public void testValidateWithFailure() throws Exception {
		setUpTemplates("CT guided [procedure] of [body structure]");
		ConceptTemplate sourceTemplate = templateService.loadOrThrow("CT guided [procedure] of [body structure]");
		ConceptTemplate destinationTemplate = templateService.loadOrThrow(destination);
		transformService.validate(sourceTemplate, destinationTemplate);
	}
	
	@Test
	public void testAllegyToSubstanceTempalteTransformation() throws Exception {
		initTestConcepts("Allergy_To_Almond_Concept.json", "Allergy_To_Almond_Concept_Trasformed.json");
		mockTerminologyServerClient();
		mockSearchConcepts(false);
		
		transformRequest.setConceptsToTransform(Collections.singleton("712839001"));
		transformRequest.setInactivationReason("ERRONEOUS");
		TemplateTransformation transformation = new TemplateTransformation("MAIN", transformRequest);
		List<Future<TransformationResult>> results = transformService.transform(transformation, terminologyServerClient);
		assertNotNull(results);
		
		List<ConceptPojo> transformed = getTransformationResults(results);

		assertFalse(transformed.isEmpty());
		assertEquals(1, transformed.size());
		ConceptPojo concept = transformed.get(0);
		// Validate descriptions transformation
		assertEquals(3, concept.getDescriptions().size());
		List<DescriptionPojo> activeTerms = concept.getDescriptions().stream().filter(DescriptionPojo::isActive).collect(Collectors.toList());
		assertEquals(2, activeTerms.size());
		for (DescriptionPojo term : activeTerms) {
			if (DescriptionPojo.Type.FSN == term.getType()) {
				assertEquals("Allergy to almond (finding)", term.getTerm());
			} else {
				assertEquals("Allergy to almond", term.getTerm());
			}
		}
		List<DescriptionPojo> inactiveTerms = concept.getDescriptions().stream().filter(d -> !d.isActive()).collect(Collectors.toList());
		assertEquals(1, inactiveTerms.size());
		assertEquals(ERRONEOUS, inactiveTerms.get(0).getInactivationIndicator());
		
		Collection<RelationshipPojo> stated = concept.getClassAxioms().iterator().next().getRelationships();
		assertEquals(3, stated.size());
		for ( RelationshipPojo pojo : stated) {
			assertNotNull(pojo.getType().getPt());
			assertNotNull("Target should not be null", pojo.getTarget());
			assertNotNull("Target concept shouldn't be null", pojo.getTarget().getConceptId());
			assertFalse(pojo.getTarget().getConceptId().isEmpty());
		}
		assertEquals(6, concept.getRelationships().size());
		Set<RelationshipPojo> inferred = concept.getRelationships()
				.stream().filter(r -> r.getCharacteristicType().equals("INFERRED_RELATIONSHIP"))
				.collect(Collectors.toSet());
		assertEquals(5, inferred.size());
		verifyTransformation(concept);
	}

	@SuppressWarnings("unchecked")
	private void mockSearchConcepts(boolean skipSearchConceptsCall) throws RestClientException {
		
		List<ConceptPojo> concepts = new ArrayList<>();
		Set<RelationshipPojo> relationships = new HashSet<>();
		for (AxiomPojo axiom : conceptToTransform.getClassAxioms()) {
			relationships.addAll(axiom.getRelationships());
		}
		if (conceptToTransform.getRelationships() != null) {
			relationships.addAll(conceptToTransform.getRelationships().stream().filter(RelationshipPojo::isActive).collect(Collectors.toSet()));
		}
		Set<ConceptMiniPojo> targets = relationships.stream().filter(RelationshipPojo::isActive).map(RelationshipPojo::getTarget).collect(Collectors.toSet());
		for (ConceptMiniPojo targetPojo : targets) {
			concepts.add(constructConceptPojo(targetPojo));
		}
		
		// Mock two method calls. One is used in template concept search and one in the transformation service
		if (skipSearchConceptsCall) {
			when(terminologyServerClient.searchConcepts(anyString(), any()))
			.thenReturn(concepts);
		} else {
			when(terminologyServerClient.searchConcepts(anyString(), any()))
			.thenReturn(Collections.singletonList(conceptToTransform), concepts);
			
		}
		// add test concepts here
		Set<ConceptMiniPojo> attributeTypes = relationships.stream().filter(RelationshipPojo::isActive).map(RelationshipPojo::getType).collect(Collectors.toSet());
		Set<ConceptMiniPojo> conceptMinis = new HashSet<>();
		conceptMinis.addAll(targets);
		conceptMinis.addAll(attributeTypes);
		conceptMinis.addAll(constructTestData());
		when(terminologyServerClient.getConceptMinis(anyString(), any(), anyInt()))
		.thenReturn(conceptMinis);
	}

	private ConceptMiniPojo constructConceptMiniPojo (String conceptId, String fsn) {
		ConceptMiniPojo pojo = new ConceptMiniPojo(conceptId);
		pojo.setFsn(new DescriptionMiniPojo(fsn, "en"));
		String pt = fsn.substring(0, fsn.indexOf("(")).trim();
		pojo.setPt(new DescriptionMiniPojo(pt, "en"));
		return pojo;
	}
	
	private Set<ConceptMiniPojo> constructTestData() {
		Set<ConceptMiniPojo> concepts = new HashSet<>();
		concepts.add(constructConceptMiniPojo("719722006", "Has realization (attribute)"));
		concepts.add(constructConceptMiniPojo("420134006", "Propensity to adverse reaction (finding)"));
		concepts.add(constructConceptMiniPojo("281647001", "Adverse reaction (disorder)"));
		concepts.add(constructConceptMiniPojo("472964009", "Allergic process (qualifier value)"));
		concepts.add(constructConceptMiniPojo("272691005", "Bone structure of shoulder girdle (body structure)"));
		concepts.add(constructConceptMiniPojo("773760007", "Traumatic event (event)"));
		concepts.add(constructConceptMiniPojo("72704001", "Fracture (morphologic abnormality)"));
		return concepts;
	}

	private ConceptPojo constructConceptPojo(ConceptMiniPojo conceptMini) {
		ConceptPojo pojo = new ConceptPojo();
		pojo.setActive(true);
		pojo.setConceptId(conceptMini.getConceptId());
		pojo.setDefinitionStatus(DefinitionStatus.valueOf(conceptMini.getDefinitionStatus()));
		pojo.setModuleId(conceptMini.getModuleId());
		Set<DescriptionPojo> descriptions = new HashSet<>();
		pojo.setDescriptions(descriptions);
		
		DescriptionPojo inactiveFsnPojo = new DescriptionPojo();
		inactiveFsnPojo.setActive(false);
		inactiveFsnPojo.setTerm("inactive_" + conceptMini.getFsn() );
		inactiveFsnPojo.setCaseSignificance(ENTIRE_TERM_CASE_SENSITIVE);
		inactiveFsnPojo.setType(FSN);
		descriptions.add(inactiveFsnPojo);
		
		DescriptionPojo fsnPojo = new DescriptionPojo();
		descriptions.add(fsnPojo);
		fsnPojo.setActive(true);
		fsnPojo.setTerm(conceptMini.getFsn().getTerm());
		fsnPojo.setCaseSignificance(CASE_INSENSITIVE);
		fsnPojo.setType(FSN);
		DescriptionPojo ptPojo = new DescriptionPojo();
		ptPojo.setTerm(TemplateUtil.getDescriptionFromFSN(conceptMini.getFsn().getTerm()));
		if (ptPojo.getTerm().equals("Aluminium")) {
			ptPojo.setAcceptabilityMap(TestDataHelper.constructAcceptabilityMap(ACCEPTABLE, PREFERRED));
			DescriptionPojo usPtPojo = new DescriptionPojo();
			usPtPojo.setTerm("Aluminum");
			usPtPojo.setAcceptabilityMap(TestDataHelper.constructAcceptabilityMap(PREFERRED, ACCEPTABLE));
			usPtPojo.setActive(true);
			usPtPojo.setType(DescriptionPojo.Type.SYNONYM);
			usPtPojo.setCaseSignificance(CASE_INSENSITIVE);
			descriptions.add(usPtPojo);
			
		} else {
			ptPojo.setAcceptabilityMap(TestDataHelper.constructAcceptabilityMap(PREFERRED, PREFERRED));
		}
		ptPojo.setActive(true);
		ptPojo.setType(DescriptionPojo.Type.SYNONYM);
		ptPojo.setCaseSignificance(CASE_INSENSITIVE);
		descriptions.add(ptPojo);
		return pojo;
	}

	@Test
	public void testAllergicReactionCausedBySubstanceTempalteTransformation() throws Exception {
		source = "Allergic reaction caused by [substance]";
		destination = "Allergic reaction caused by [substance] (disorder) V2";
		setUpTemplates(source, destination);
		initTestConcepts("Allergic_Reaction_Caused_By_Adhesive_Concept.json", "Allergic_Reaction_Caused_By_Adhesive_Concept_Transformed.json");
		mockTerminologyServerClient();
		mockSearchConcepts(false);
		
		TemplateTransformRequest transformRequest = new TemplateTransformRequest(source, destination);
		transformRequest.setConceptsToTransform(Collections.singleton("418325008"));
		TemplateTransformation transformation = new TemplateTransformation("MAIN", transformRequest);
		List<Future<TransformationResult>> results = transformService.transform(transformation, terminologyServerClient);
		assertNotNull(results);
		
		List<ConceptPojo> transformed = getTransformationResults(results);
		assertFalse(transformed.isEmpty());
		assertEquals(1, transformed.size());
		ConceptPojo concept = transformed.get(0);
		//validate descriptions transformation
		assertEquals(6, concept.getDescriptions().size());
		List<DescriptionPojo> activeTerms = concept.getDescriptions().stream().filter(DescriptionPojo::isActive).collect(Collectors.toList());
		assertEquals(4, activeTerms.size());
		String[] activeSynonyms = {"Allergic reaction caused by adhesive agent",
				"Allergic reaction to adhesive",
				"Allergic reaction caused by adhesive"};
		
		for (DescriptionPojo term : activeTerms) {
			if (DescriptionPojo.Type.FSN == term.getType()) {
				assertEquals("Allergic reaction caused by adhesive agent (disorder)", term.getTerm());
			} else {
				if (term.getTerm().equals(activeSynonyms[0])) {
					assertNotNull(term.getAcceptabilityMap());
					assertTrue(term.getAcceptabilityMap().containsValue(PREFERRED));
				}
			}
		}
		List<DescriptionPojo> inactiveTerms = concept.getDescriptions().stream().filter(d -> !d.isActive()).collect(Collectors.toList());
		assertEquals(2, inactiveTerms.size());
		Collection<RelationshipPojo> classAxiomRels = concept.getClassAxioms().iterator().next().getRelationships();
		assertEquals(3, classAxiomRels.size());
		for ( RelationshipPojo pojo : classAxiomRels) {
			assertNotNull(pojo.getType().getPt());
			assertNotNull("Target should not be null", pojo.getTarget());
			assertNotNull("Target concept shouldn't be null", pojo.getTarget().getConceptId());
			assertFalse(pojo.getTarget().getConceptId().isEmpty());
		}
		
		assertEquals(6, concept.getRelationships().size());
		Set<RelationshipPojo> inferred = concept.getRelationships()
				.stream().filter(r -> r.getCharacteristicType().equals("INFERRED_RELATIONSHIP"))
				.collect(Collectors.toSet());
		assertEquals(6, inferred.size());

		assertEquals(transformedConcept.getClassAxioms(), transformedConcept.getClassAxioms());
		assertEquals(concept.getClassAxioms(), concept.getClassAxioms());
		assertEquals(transformedConcept.getClassAxioms(), concept.getClassAxioms());

		verifyTransformation(concept);
	}

	
	@Test
	public void testAllergyToAluminiumTransformation() throws Exception {
		initTestConcepts("Allergy_to_Aluminium_Concept.json", "Allergy_to_Aluminium_Concept_Transformed.json");
		mockTerminologyServerClient();
		mockSearchConcepts(false);
		
		transformRequest.setConceptsToTransform(Collections.singleton("402306009"));
		TemplateTransformation transformation = new TemplateTransformation("MAIN", transformRequest);
		List<Future<TransformationResult>> results = transformService.transform(transformation, terminologyServerClient);
		assertNotNull(results);
		
		List<ConceptPojo> transformed = getTransformationResults(results);
		assertFalse(transformed.isEmpty());
		assertEquals(1, transformed.size());
		ConceptPojo concept = transformed.get(0);
		//validate descriptions transformation
		assertEquals(7, concept.getDescriptions().size());
		List<DescriptionPojo> activeTerms = concept.getDescriptions().stream().filter(DescriptionPojo::isActive).collect(Collectors.toList());
		assertEquals(5, activeTerms.size());
		verifyTransformation(concept);
	}
	
	
	@Test
	public void testSingleConceptTranformation() throws Exception {
		initTestConcepts("Allergy_to_Aluminium_Concept_WithAxiomOnly.json", "Allergy_to_Aluminium_Concept_WithAxiomOnly_Transformed.json");
		Set<String> concepts = new HashSet<>();
		concepts.add("402306009");
		mockTerminologyServerClient();
		mockSearchConcepts(true);
		
		transformRequest = new TemplateTransformRequest(null, destination);
		ConceptPojo result = transformService.transformConcept("MAIN", transformRequest, conceptToTransform, terminologyServerClient);
		assertNotNull(result);
		
		// validate descriptions transformation
		assertEquals(7, result.getDescriptions().size());
		List<DescriptionPojo> activeTerms = result.getDescriptions().stream().filter(DescriptionPojo::isActive).collect(Collectors.toList());
		assertEquals(5, activeTerms.size());
		verifyTransformation(result);
	}
	
	@Test
	public void testMultipleSlotsWithinTheSameAttributeType() throws Exception {
		String tempalteName = "Fracture dislocation of [body structure] (disorder)";
		setUpTemplates(tempalteName);
		initTestConcepts("Fracture_dislocation_of_elbow_joint_New_Concept.json", "Fracture_dislocation_of_elbow_joint_transformed.json");
		mockTerminologyServerClient();
		mockSearchConcepts(true);
		mockSearchAttributeValuesWithinRange("<<39352004 |Joint structure (body structure)|", Collections.singletonList("16953009"));
		mockSearchAttributeValuesWithinRange("<<72704001 |Fracture (morphologic abnormality)|", Collections.singletonList("72704001"));
		mockSearchAttributeValuesWithinRange("<<272673000 |Bone structure (body structure)|", Collections.singletonList("305016004"));
		mockSearchAttributeValuesWithinRange("<<87642003 |Dislocation (morphologic abnormality)|", Collections.singletonList("87642003"));
		mockSearchAttributeValuesWithinRange("<<773760007 |Traumatic event (event)|", Collections.singletonList("773760007"));
		// invoke the first mock method call
		terminologyServerClient.searchConcepts("MAIN", new ArrayList<>(Collections.singleton("123")));
		ConceptPojo result = transformService.transformConcept("MAIN/test", new TemplateTransformRequest(null, tempalteName),
				conceptToTransform, terminologyServerClient);
		verifyTransformation(result);
	}
	
	private void mockSearchAttributeValuesWithinRange(String rangeEcl, List<String> conceptIds) throws RestClientException {
		when(terminologyServerClient.eclQuery(anyString(), contains(rangeEcl), anyInt()))
		.thenReturn(new HashSet<String>(conceptIds));
	}

	private List<ConceptPojo> getTransformationResults(List<Future<TransformationResult>> results) {
		List<ConceptPojo> transformed = new ArrayList<>();
		Map<String, String> errorMsgMap = new HashMap<>();
		for (Future<TransformationResult> future : results) {
			try {
				TransformationResult transformationResult = future.get();
				Map<String, String> failures = transformationResult.getFailures();
				if (failures != null && !failures.isEmpty()) {
					StringBuilder msgBuilder = new StringBuilder();
					msgBuilder.append("Unexpected failure \n");
					for (String conceptId : failures.keySet()) {
						msgBuilder.append("ConceptId=").append(conceptId).append(" failure msg = ").append(failures.get(conceptId)).append("\n");
					}
					fail("Shouldn't have failures!" + msgBuilder.toString());
				}
				transformed.addAll(transformationResult.getConcepts());
				for (String key : transformationResult.getFailures().keySet()) {
					errorMsgMap.put(key, transformationResult.getFailures().get(key));
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				fail("No exceptions should be thrown");
			}
		}
		return transformed;
	}
	
	private void setUpTemplates(String... templates) throws Exception {
		for (String templateName : templates) {
			FileUtils.copyFileToDirectory(new File(getClass().getResource("/templates/" + templateName + ".json").toURI()),
					templateJsonStore.getStoreDirectory());
		}
		templateService.reloadCache();
	}
	
	private void initTestConcepts(String sourceConceptJson, String transformedJson) throws IOException {
		InputStream resourceAsStream = getClass().getResourceAsStream(sourceConceptJson);
		assertNotNull(sourceConceptJson + " stream must not be null", resourceAsStream);
		try (Reader sourceConceptReader = new InputStreamReader(resourceAsStream, UTF_8);
			 Reader transformedJsonReader = new InputStreamReader(getClass().getResourceAsStream(transformedJson), UTF_8)) {
				conceptToTransform = gson.fromJson(sourceConceptReader, ConceptPojo.class);
				transformedConcept = gson.fromJson(transformedJsonReader, ConceptPojo.class);
			}
	}

	private OngoingStubbing<SnowstormRestClient> mockTerminologyServerClient() {
		return when(clientFactory.getClient()).thenReturn(terminologyServerClient);
	}
	
	private void verifyTransformation(ConceptPojo transformed) {
		if (isDebug) {
			System.out.println(gson.toJson(transformed));
		}
//		JSONAssert.assertEquals(gson.toJson(transformedConcept), gson.toJson(transformed), false);
		if (!transformedConcept.equals(transformed)) {
			assertEquals(transformedConcept.toString().replace(",", ",\n"), transformed.toString().replace(",", ",\n"));
		}
	}
}
