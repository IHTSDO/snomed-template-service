package org.ihtsdo.otf.authoringtemplate.transform.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.ihtsdo.otf.authoringtemplate.Config;
import org.ihtsdo.otf.authoringtemplate.TestConfig;
import org.ihtsdo.otf.authoringtemplate.service.ConceptTemplateSearchService;
import org.ihtsdo.otf.authoringtemplate.service.Constants;
import org.ihtsdo.otf.authoringtemplate.service.JsonStore;
import org.ihtsdo.otf.authoringtemplate.service.TemplateService;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.authoringtemplate.transform.TemplateTransformRequest;
import org.ihtsdo.otf.authoringtemplate.transform.TemplateTransformation;
import org.ihtsdo.otf.authoringtemplate.transform.TransformationResult;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClient;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClientFactory;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.RelationshipPojo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.OngoingStubbing;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.DescriptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, TestConfig.class})
public class ConceptTemplateTransformServiceTest {
	
	private static final String TEMPLATES = "/templates/";

	private static final String JSON = ".json";
	
	@MockBean
	private ConceptTemplateSearchService searchService;
	
	@Autowired
	private ConceptTemplateTransformService transformService;
	
	@MockBean
	private SnowOwlRestClientFactory clientFactory;

	@MockBean
	private SnowOwlRestClient terminologyServerClient;
	
	@Autowired
	private TemplateService templateService;

	@Autowired
	private JsonStore jsonStore;
	
	private String source = "Allergy to [substance]";
	private String destination = "Allergy to [substance] V2";

	private static final String CT_GUIDED_BODY_STRUCTURE_TEMPLATE = "CT guided [procedure] of [body structure]";
	
	private ConceptPojo conceptToTransform;
	private ConceptPojo conceptTransformed;
	private Gson gson;
	
	@Before
	public void setUp() throws Exception {
		gson = new GsonBuilder().setPrettyPrinting().create();
		setUpTestTemplates("Allergy_To_Almond_Concept.json", "Allergy_To_Almond_Concept_Trasformed.json");
	}

	private void setUpTestTemplates(String conceptToTransfom, String transformed)
			throws IOException, URISyntaxException, ServiceException, UnsupportedEncodingException {
		FileUtils.copyFileToDirectory(new File(getClass().getResource(TEMPLATES + source + JSON).toURI()),
				jsonStore.getStoreDirectory());
		
		FileUtils.copyFileToDirectory(new File(getClass().getResource(TEMPLATES + destination + JSON).toURI()),
				jsonStore.getStoreDirectory());
		
		FileUtils.copyFileToDirectory(new File(getClass().getResource(TEMPLATES + CT_GUIDED_BODY_STRUCTURE_TEMPLATE + JSON).toURI()),
				jsonStore.getStoreDirectory());
		templateService.reloadCache();
		try (Reader conceptJsonReader = new InputStreamReader(getClass().getResourceAsStream(conceptToTransfom), Constants.UTF_8);
			 Reader expectedJsonReader = new InputStreamReader(getClass().getResourceAsStream(transformed), Constants.UTF_8)) {
			conceptToTransform = gson.fromJson(conceptJsonReader, ConceptPojo.class);
			conceptTransformed = gson.fromJson(expectedJsonReader, ConceptPojo.class);
		}
	}
	
	@Test
	public void testCreateTemplateTransformation() throws ServiceException {
		TemplateTransformRequest transformRequest = new TemplateTransformRequest();
		Set<String> concepts = new HashSet<>();
		concepts.add("123555");
		transformRequest.setConceptsToTransform(concepts);
		transformRequest.setSourceTemplate(source);
		TemplateTransformation transformation = transformService.createTemplateTransformation("MAIN", destination, transformRequest);
		assertNotNull(transformation);
	}
	
	@Test
	public void testValidateWithSuccess() {
		try {
			ConceptTemplate sourceTemplate = templateService.loadOrThrow(source);
			ConceptTemplate destinationTemplate = templateService.loadOrThrow(destination);
			transformService.validate(sourceTemplate, destinationTemplate);
		} catch (Exception e) {
			Assert.fail("No exception is expected to be thrown." + e.getMessage());
		}
	}
	
	
	@Test(expected=ServiceException.class)
	public void testValidateWithFailure() throws Exception {
		ConceptTemplate sourceTemplate = templateService.loadOrThrow(CT_GUIDED_BODY_STRUCTURE_TEMPLATE);
		ConceptTemplate destinationTemplate = templateService.loadOrThrow(destination);
		transformService.validate(sourceTemplate, destinationTemplate);
	}
	
	@Test
	public void testAllegyToSubstanceTempalteTransformation() throws Exception {
		Set<String> concepts = new HashSet<>();
		concepts.add("712839001");
		mockTerminologyServerClient();
		when(terminologyServerClient.searchConcepts(anyString(),any()))
		.thenReturn(Arrays.asList(conceptToTransform));
		
		TemplateTransformRequest transformRequest = new TemplateTransformRequest();
		transformRequest.setConceptsToTransform(concepts);
		transformRequest.setSourceTemplate(source);
		transformRequest.setInactivationReason("ERRONEOUS");
		TemplateTransformation transformation = new TemplateTransformation("MAIN", destination, transformRequest);
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
		
		assertEquals(10, concept.getRelationships().size());
		List<RelationshipPojo> stated = concept.getRelationships()
				.stream().filter(r -> r.getCharacteristicType().equals("STATED_RELATIONSHIP"))
				.collect(Collectors.toList());
		assertEquals(5, stated.size());
		for ( RelationshipPojo pojo : stated) {
			assertNotNull("Target should not be null", pojo.getTarget());
			assertNotNull("Target concept shouldn't be null", pojo.getTarget().getConceptId());
			assertTrue(!pojo.getTarget().getConceptId().isEmpty());
		}
		
		Set<RelationshipPojo> inferred = concept.getRelationships()
				.stream().filter(r -> r.getCharacteristicType().equals("INFERRED_RELATIONSHIP"))
				.collect(Collectors.toSet());
		Gson gson =  new GsonBuilder().setPrettyPrinting().create();
		for (ConceptPojo pojo : transformed) {
			System.out.println(gson.toJson(pojo));
		}
		assertEquals(5, inferred.size());
		assertEquals(conceptTransformed, concept);
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
		when(terminologyServerClient.searchConcepts(anyString(),any()))
		.thenReturn(Arrays.asList(conceptToTransform));
		
		TemplateTransformRequest transformRequest = new TemplateTransformRequest();
		transformRequest.setConceptsToTransform(concepts);
		transformRequest.setSourceTemplate(source);
		TemplateTransformation transformation = new TemplateTransformation("MAIN", destination, transformRequest);
		List<Future<TransformationResult>> results = transformService.transform(transformation, terminologyServerClient);
		assertNotNull(results);
		
		List<ConceptPojo> transformed = getTransformationResults(results);
		assertEquals(true, !transformed.isEmpty());
		assertEquals(1, transformed.size());
		ConceptPojo concept = transformed.get(0);
		//validate descriptions transformation
		assertEquals(6, concept.getDescriptions().size());
		List<DescriptionPojo> activeTerms = concept.getDescriptions().stream().filter(d -> d.isActive()).collect(Collectors.toList());
		assertEquals(2, activeTerms.size());
		for (DescriptionPojo term : activeTerms) {
			if (DescriptionType.FSN.name().equals(term.getType())) {
				assertEquals("Allergic reaction caused by adhesive agent (disorder)", term.getTerm());
			} else {
				assertEquals("Allergic reaction caused by adhesive agent", term.getTerm());
			}
		}
		List<DescriptionPojo> inactiveTerms = concept.getDescriptions().stream().filter(d -> !d.isActive()).collect(Collectors.toList());
		assertEquals(4, inactiveTerms.size());
		
		assertEquals(11, concept.getRelationships().size());
		List<RelationshipPojo> stated = concept.getRelationships()
				.stream().filter(r -> r.getCharacteristicType().equals("STATED_RELATIONSHIP"))
				.collect(Collectors.toList());
		assertEquals(5, stated.size());
		for ( RelationshipPojo pojo : stated) {
			assertNotNull("Target should not be null", pojo.getTarget());
			assertNotNull("Target concept shouldn't be null", pojo.getTarget().getConceptId());
			assertTrue(!pojo.getTarget().getConceptId().isEmpty());
		}
		
		Set<RelationshipPojo> inferred = concept.getRelationships()
				.stream().filter(r -> r.getCharacteristicType().equals("INFERRED_RELATIONSHIP"))
				.collect(Collectors.toSet());
		Gson gson =  new GsonBuilder().setPrettyPrinting().create();
		for (ConceptPojo pojo : transformed) {
			System.out.println(gson.toJson(pojo));
		}
		assertEquals(6, inferred.size());
		assertEquals(conceptTransformed.toString().replace(",", ",\n"), concept.toString().replace(",", ",\n"));
	}

	private List<ConceptPojo> getTransformationResults(List<Future<TransformationResult>> results) {
		List<ConceptPojo> transformed = new ArrayList<>();
		Map<String, String> errorMsgMap = new HashMap<>();
		for (Future<TransformationResult> future : results) {
			try {
				TransformationResult transformationResult = future.get();
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

	private OngoingStubbing<SnowOwlRestClient> mockTerminologyServerClient() {
		return when(clientFactory.getClient()).thenReturn(terminologyServerClient);
	}
}
