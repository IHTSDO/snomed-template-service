package org.ihtsdo.otf.authoringtemplate.transform.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.ihtsdo.otf.authoringtemplate.service.AbstractServiceTest;
import org.ihtsdo.otf.authoringtemplate.service.Constants;
import org.ihtsdo.otf.authoringtemplate.service.JsonStore;
import org.ihtsdo.otf.authoringtemplate.service.TemplateConceptSearchService;
import org.ihtsdo.otf.authoringtemplate.service.TemplateUtil;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.authoringtemplate.transform.TemplateTransformRequest;
import org.ihtsdo.otf.authoringtemplate.transform.TemplateTransformation;
import org.ihtsdo.otf.authoringtemplate.transform.TestDataHelper;
import org.ihtsdo.otf.authoringtemplate.transform.TransformationResult;
import org.ihtsdo.otf.rest.client.RestClientException;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowOwlRestClient;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptMiniPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptMiniPojo.DescriptionMiniPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DefinitionStatus;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RelationshipPojo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.OngoingStubbing;
import org.snomed.authoringtemplate.domain.CaseSignificance;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.DescriptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
public class TemplateConceptTransformServiceTest extends AbstractServiceTest {
	
	private static final String TEMPLATES = "/templates/";

	private static final String JSON = ".json";
	
	@MockBean
	private TemplateConceptSearchService searchService;
	
	@Autowired
	private TemplateConceptTransformService transformService;

	@Autowired
	private JsonStore jsonStore;
	
	private String source = "Allergy to [substance]";
	private String destination = "Allergy to [substance] V2";

	private static final String CT_GUIDED_BODY_STRUCTURE_TEMPLATE = "CT guided [procedure] of [body structure]";
	
	private ConceptPojo conceptToTransform;
	private ConceptPojo transformedConcept;
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	// set it to true to print out concept in json
	private boolean isDebug = false;
	
	private TemplateTransformRequest transformRequest;
	
	@Before
	public void setUp() {
		transformRequest = new TemplateTransformRequest(source, destination);
	}
	
	@Test
	public void testCreateTemplateTransformation() throws ServiceException {
		Set<String> concepts = new HashSet<>();
		concepts.add("123555");
		transformRequest.setConceptsToTransform(concepts);
		TemplateTransformation transformation = transformService.createTemplateTransformation("MAIN", transformRequest);
		assertNotNull(transformation);
	}
	
	@Test
	public void testValidateWithSuccess() {
		try {
			FileUtils.copyFileToDirectory(new File(getClass().getResource(TEMPLATES + source + JSON).toURI()),
					jsonStore.getStoreDirectory());
			
			FileUtils.copyFileToDirectory(new File(getClass().getResource(TEMPLATES + destination + JSON).toURI()),
					jsonStore.getStoreDirectory());
			templateService.reloadCache();
			ConceptTemplate sourceTemplate = templateService.loadOrThrow(source);
			ConceptTemplate destinationTemplate = templateService.loadOrThrow(destination);
			transformService.validate(sourceTemplate, destinationTemplate);
		} catch (Exception e) {
			Assert.fail("No exception is expected to be thrown." + e.getMessage());
		}
	}
	
	
	@Test(expected=ServiceException.class)
	public void testValidateWithFailure() throws Exception {
		setUpTestTemplates("Allergy_To_Almond_Concept.json", "Allergy_To_Almond_Concept_Trasformed.json");
		ConceptTemplate sourceTemplate = templateService.loadOrThrow(CT_GUIDED_BODY_STRUCTURE_TEMPLATE);
		ConceptTemplate destinationTemplate = templateService.loadOrThrow(destination);
		transformService.validate(sourceTemplate, destinationTemplate);
	}
	
	@Test
	public void testAllegyToSubstanceTempalteTransformation() throws Exception {
		setUpTestTemplates("Allergy_To_Almond_Concept.json", "Allergy_To_Almond_Concept_Trasformed.json");
		Set<String> concepts = new HashSet<>();
		concepts.add("712839001");
		mockTerminologyServerClient();
		mockSearchConcepts();
		
		transformRequest.setConceptsToTransform(concepts);
		transformRequest.setInactivationReason("ERRONEOUS");
		TemplateTransformation transformation = new TemplateTransformation("MAIN", transformRequest);
		List<Future<TransformationResult>> results = transformService.transform(transformation, terminologyServerClient);
		assertNotNull(results);
		
		List<ConceptPojo> transformed = getTransformationResults(results);
		
		assertEquals(true, !transformed.isEmpty());
		assertEquals(1, transformed.size());
		ConceptPojo concept = transformed.get(0);
		//validate descriptions transformation
		assertEquals(3, concept.getDescriptions().size());
		List<DescriptionPojo> activeTerms = concept.getDescriptions().stream().filter(d -> d.isActive()).collect(Collectors.toList());
		assertEquals(2, activeTerms.size());
		for (DescriptionPojo term : activeTerms) {
			if (DescriptionType.FSN.name().equals(term.getType())) {
				assertEquals("Allergy to almond (finding)", term.getTerm());
			} else {
				assertEquals("Allergy to almond", term.getTerm());
			}
		}
		List<DescriptionPojo> inactiveTerms = concept.getDescriptions().stream().filter(d -> !d.isActive()).collect(Collectors.toList());
		assertEquals(1, inactiveTerms.size());
		assertEquals("ERRONEOUS", inactiveTerms.get(0).getInactivationIndicator());
		
		Collection<RelationshipPojo> stated = concept.getClassAxioms().iterator().next().getRelationships();
		assertEquals(3, stated.size());
		for ( RelationshipPojo pojo : stated) {
			assertNotNull(pojo.getType().getPt());
			assertNotNull("Target should not be null", pojo.getTarget());
			assertNotNull("Target concept shouldn't be null", pojo.getTarget().getConceptId());
			assertTrue(!pojo.getTarget().getConceptId().isEmpty());
		}
		assertEquals(6, concept.getRelationships().size());
		Set<RelationshipPojo> inferred = concept.getRelationships()
				.stream().filter(r -> r.getCharacteristicType().equals("INFERRED_RELATIONSHIP"))
				.collect(Collectors.toSet());
		assertEquals(5, inferred.size());
		verifyTransformation(concept);
	}

	@SuppressWarnings("unchecked")
	private void mockSearchConcepts() throws RestClientException {
		
		List<ConceptPojo> concepts = new ArrayList<>();
		Set<ConceptMiniPojo> targets = conceptToTransform.getRelationships().stream().filter(r -> r.isActive()).map(r -> r.getTarget()).collect(Collectors.toSet());
		for (ConceptMiniPojo targetPojo : targets) {
			concepts.add(constructConceptPojo(targetPojo));
		}
		when(terminologyServerClient.searchConcepts(anyString(), any()))
		.thenReturn(Arrays.asList(conceptToTransform), concepts);
		
		// add test concepts here
		Set<ConceptMiniPojo> attributeTypes = conceptToTransform.getRelationships().stream().filter(r -> r.isActive()).map(r -> r.getType()).collect(Collectors.toSet());
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
		inactiveFsnPojo.setCaseSignificance(CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE.name());
		inactiveFsnPojo.setType(DescriptionType.FSN.name());
		descriptions.add(inactiveFsnPojo);
		
		DescriptionPojo fsnPojo = new DescriptionPojo();
		descriptions.add(fsnPojo);
		fsnPojo.setActive(true);
		fsnPojo.setTerm(conceptMini.getFsn().getTerm());
		fsnPojo.setCaseSignificance(CaseSignificance.CASE_INSENSITIVE.name());
		fsnPojo.setType(DescriptionType.FSN.name());
		DescriptionPojo ptPojo = new DescriptionPojo();
		ptPojo.setTerm(TemplateUtil.getDescriptionFromFSN(conceptMini.getFsn().getTerm()));
		if (ptPojo.getTerm().equals("Aluminium")) {
			ptPojo.setAcceptabilityMap(TestDataHelper.constructAcceptabilityMap(Constants.ACCEPTABLE, Constants.PREFERRED));
			DescriptionPojo usPtPojo = new DescriptionPojo();
			usPtPojo.setTerm("Aluminum");
			usPtPojo.setAcceptabilityMap(TestDataHelper.constructAcceptabilityMap(Constants.PREFERRED,Constants.ACCEPTABLE));
			usPtPojo.setActive(true);
			usPtPojo.setType(DescriptionType.SYNONYM.name());
			usPtPojo.setCaseSignificance(CaseSignificance.CASE_INSENSITIVE.name());
			descriptions.add(usPtPojo);
			
		} else {
			ptPojo.setAcceptabilityMap(TestDataHelper.constructAcceptabilityMap(Constants.PREFERRED, Constants.PREFERRED));
		}
		ptPojo.setActive(true);
		ptPojo.setType(DescriptionType.SYNONYM.name());
		ptPojo.setCaseSignificance(CaseSignificance.CASE_INSENSITIVE.name());
		descriptions.add(ptPojo);
		return pojo;
	}

	@Test
	public void testAllergicReactionCausedBySubstanceTempalteTransformation() throws Exception {
		source = "Allergic reaction caused by [substance]";
		destination = "Allergic reaction caused by [substance] (disorder) V2";
		setUpTestTemplates("Allergic_Reaction_Caused_By_Adhesive_Concept.json",
				"Allergic_Reaction_Caused_By_Adhesive_Concept_Transformed.json");
		Set<String> concepts = new HashSet<>();
		concepts.add("418325008");
		mockTerminologyServerClient();
		mockSearchConcepts();
		
		TemplateTransformRequest transformRequest = new TemplateTransformRequest(source, destination);
		transformRequest.setConceptsToTransform(concepts);
		TemplateTransformation transformation = new TemplateTransformation("MAIN", transformRequest);
		List<Future<TransformationResult>> results = transformService.transform(transformation, terminologyServerClient);
		assertNotNull(results);
		
		List<ConceptPojo> transformed = getTransformationResults(results);
		assertEquals(true, !transformed.isEmpty());
		assertEquals(1, transformed.size());
		ConceptPojo concept = transformed.get(0);
		//validate descriptions transformation
		assertEquals(6, concept.getDescriptions().size());
		List<DescriptionPojo> activeTerms = concept.getDescriptions().stream().filter(d -> d.isActive()).collect(Collectors.toList());
		assertEquals(4, activeTerms.size());
		String[] activeSynonyms = {"Allergic reaction caused by adhesive agent",
				"Allergic reaction to adhesive",
				"Allergic reaction caused by adhesive"};
		
		for (DescriptionPojo term : activeTerms) {
			if (DescriptionType.FSN.name().equals(term.getType())) {
				assertEquals("Allergic reaction caused by adhesive agent (disorder)", term.getTerm());
			} else {
				Arrays.asList(activeSynonyms).contains(term.getTerm());
				if (term.getTerm().equals(activeSynonyms[0])) {
					assertNotNull(term.getAcceptabilityMap());
					assertTrue(term.getAcceptabilityMap().values().contains(Constants.PREFERRED));
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
			assertTrue(!pojo.getTarget().getConceptId().isEmpty());
		}
		
		assertEquals(6, concept.getRelationships().size());
		Set<RelationshipPojo> inferred = concept.getRelationships()
				.stream().filter(r -> r.getCharacteristicType().equals("INFERRED_RELATIONSHIP"))
				.collect(Collectors.toSet());
		assertEquals(6, inferred.size());
		verifyTransformation(concept);
	}

	
	@Test
	public void testAllergyToAluminiumTransformation() throws Exception {
		setUpTestTemplates("Allergy_to_Aluminium_Concept.json",
				"Allergy_to_Aluminium_Concept_Transformed.json");
		Set<String> concepts = new HashSet<>();
		concepts.add("402306009");
		mockTerminologyServerClient();
		mockSearchConcepts();
		
		transformRequest.setConceptsToTransform(concepts);
		TemplateTransformation transformation = new TemplateTransformation("MAIN", transformRequest);
		List<Future<TransformationResult>> results = transformService.transform(transformation, terminologyServerClient);
		assertNotNull(results);
		
		List<ConceptPojo> transformed = getTransformationResults(results);
		assertEquals(true, !transformed.isEmpty());
		assertEquals(1, transformed.size());
		ConceptPojo concept = transformed.get(0);
		//validate descriptions transformation
		assertEquals(7, concept.getDescriptions().size());
		List<DescriptionPojo> activeTerms = concept.getDescriptions().stream().filter(d -> d.isActive()).collect(Collectors.toList());
		assertEquals(5, activeTerms.size());
		verifyTransformation(concept);
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
						msgBuilder.append("ConceptId=" + conceptId + " failure msg = " + failures.get(conceptId) + "\n");
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
	
	private void setUpTestTemplates(String sourceTempalte, String destinationTemplate)
			throws IOException, URISyntaxException, ServiceException, UnsupportedEncodingException {
		FileUtils.copyFileToDirectory(new File(getClass().getResource(TEMPLATES + source + JSON).toURI()),
				jsonStore.getStoreDirectory());
		
		FileUtils.copyFileToDirectory(new File(getClass().getResource(TEMPLATES + destination + JSON).toURI()),
				jsonStore.getStoreDirectory());
		
		FileUtils.copyFileToDirectory(new File(getClass().getResource(TEMPLATES + CT_GUIDED_BODY_STRUCTURE_TEMPLATE + JSON).toURI()),
				jsonStore.getStoreDirectory());
		templateService.reloadCache();
		try (Reader sourceConceptReader = new InputStreamReader(getClass().getResourceAsStream(sourceTempalte), Constants.UTF_8);
			 Reader transformedJsonReader = new InputStreamReader(getClass().getResourceAsStream(destinationTemplate), Constants.UTF_8)) {
			conceptToTransform = gson.fromJson(sourceConceptReader, ConceptPojo.class);
			transformedConcept = gson.fromJson(transformedJsonReader, ConceptPojo.class);
		}
	}

	private OngoingStubbing<SnowOwlRestClient> mockTerminologyServerClient() {
		return when(clientFactory.getClient()).thenReturn(terminologyServerClient);
	}
	
	private void verifyTransformation(ConceptPojo transformed) {
		if (isDebug) {
			System.out.println(gson.toJson(transformed));
		}
		if (!transformedConcept.equals(transformed)) {
			assertEquals(transformedConcept.toString().replace(",", ",\n"), transformed.toString().replace(",", ",\n"));
		}
	}
}
