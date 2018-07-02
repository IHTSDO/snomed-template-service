package org.ihtsdo.otf.authoringtemplate.transform.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
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
import org.snomed.authoringtemplate.domain.*;
import org.snomed.authoringtemplate.domain.logical.*;
import org.ihtsdo.otf.authoringtemplate.service.ConceptTemplateSearchService;
import org.ihtsdo.otf.authoringtemplate.service.Constants;
import org.ihtsdo.otf.authoringtemplate.service.JsonStore;
import org.ihtsdo.otf.authoringtemplate.service.TemplateService;
import org.ihtsdo.otf.authoringtemplate.service.TemplateTransformation;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.authoringtemplate.transform.TemplateTransformRequest;
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
	
	private String source;
	private String destination;

	private static final String CT_GUIDED_BODY_STRUCTURE_TEMPLATE = "CT guided [procedure] of [body structure]";
	
	private ConceptPojo conceptToTransform;
	private Gson gson;
	
	@Before
	public void setUp() throws Exception {
		source = "Allergy to [substance]";
		FileUtils.copyFileToDirectory(new File(getClass().getResource(TEMPLATES + source + JSON).toURI()),
				jsonStore.getStoreDirectory());
		destination = "Allergy to [substance] V2";
		FileUtils.copyFileToDirectory(new File(getClass().getResource(TEMPLATES + destination + JSON).toURI()),
				jsonStore.getStoreDirectory());
		
		FileUtils.copyFileToDirectory(new File(getClass().getResource(TEMPLATES + CT_GUIDED_BODY_STRUCTURE_TEMPLATE + JSON).toURI()),
				jsonStore.getStoreDirectory());
		templateService.reloadCache();
		gson = new GsonBuilder().setPrettyPrinting().create();
		try (Reader conceptJsonReader = new InputStreamReader( getClass().getResourceAsStream("concept.json"), Constants.UTF_8)) {
			conceptToTransform = gson.fromJson(conceptJsonReader, ConceptPojo.class);
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
			ConceptTemplate sourceTemplate = jsonStore.load(source, ConceptTemplate.class);
			ConceptTemplate destinationTemplate = jsonStore.load(destination, ConceptTemplate.class);
			transformService.validate(sourceTemplate, destinationTemplate);
		} catch (Exception e) {
			Assert.fail("No exception is expected to be thrown");
		}
	}
	
	
	@Test(expected=ServiceException.class)
	public void testValidateWithFailure() throws Exception {
		ConceptTemplate sourceTemplate = jsonStore.load(CT_GUIDED_BODY_STRUCTURE_TEMPLATE, ConceptTemplate.class);
		ConceptTemplate destinationTemplate = jsonStore.load(destination, ConceptTemplate.class);
		transformService.validate(sourceTemplate, destinationTemplate);
	}
	
	@Test
	public void testConceptTransformation() throws Exception {
		Set<String> concepts = new HashSet<>();
		concepts.add("712839001");
		
		mockTerminologyServerClient();
		when(terminologyServerClient.searchConcepts(anyString(),any()))
		.thenReturn(Arrays.asList(conceptToTransform));
		
		TemplateTransformRequest transformRequest = new TemplateTransformRequest();
		transformRequest.setConceptsToTransform(concepts);
		transformRequest.setSourceTemplate(source);
		TemplateTransformation transformation = new TemplateTransformation("MAIN", destination, transformRequest);
		List<Future<TransformationResult>> results = transformService.transform(transformation, terminologyServerClient);
		assertNotNull(results);
		
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
		assertEquals(true, !transformed.isEmpty());
		assertEquals(1, transformed.size());
		ConceptPojo concept = transformed.get(0);
		assertEquals(10, concept.getRelationships().size());
		List<RelationshipPojo> stated = concept.getRelationships()
				.stream().filter(r -> r.getCharacteristicType().equals("STATED_RELATIONSHIP"))
				.collect(Collectors.toList());
		assertEquals(6, stated.size());
		for ( RelationshipPojo pojo : stated) {
			assertNotNull("Target should not be null", pojo.getTarget());
			assertNotNull("Target concept shouldn't be null", pojo.getTarget().getConceptId());
			assertTrue(!pojo.getTarget().getConceptId().isEmpty());
		}
		
		Set<RelationshipPojo> inferred = concept.getRelationships()
				.stream().filter(r -> r.getCharacteristicType().equals("INFERRED_RELATIONSHIP"))
				.collect(Collectors.toSet());
		//Remove inferred and only display stated for manual checking
		concept.getRelationships().removeAll(inferred);
		Gson gson =  new GsonBuilder().setPrettyPrinting().create();
		for (ConceptPojo pojo : transformed) {
			System.out.println(gson.toJson(pojo));
		}
		
	}

	private ConceptTemplate createConceptTemplate() {
		ConceptTemplate template = new ConceptTemplate();
		ConceptOutline conceptOutline = new ConceptOutline();
		conceptOutline.setDefinitionStatus(DefinitionStatus.PRIMITIVE);
		conceptOutline.setModuleId("900000000000207008");
		List<Description> descriptions = createDescriptions();
		conceptOutline.setDescriptions(descriptions);
		List<Relationship> relationships = createRelationships();
		conceptOutline.setRelationships(relationships);
		template.setConceptOutline(conceptOutline);
		return template;
	}

	private List<Relationship> createRelationships() {
		List<Relationship> relationships = new ArrayList<>();
		Relationship rel = new Relationship();
		rel.setTarget(new ConceptMini("234780"));
		rel.setType(new ConceptMini("116680003"));
		rel.setGroupId(0);
		relationships.add(rel);
		return relationships;
	}

	private List<Description> createDescriptions() {
		List<Description> descriptions = new ArrayList<>();
		Description fsn = new Description();
		fsn.setCaseSignificance(CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE);
		fsn.setType(DescriptionType.FSN);
		fsn.setTerm("New Term (test)");
		descriptions.add(fsn);
		
		Description synonym = new Description();
		synonym.setCaseSignificance(CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE);
		synonym.setType(DescriptionType.SYNONYM);
		synonym.setTerm("New Term");
		descriptions.add(synonym);
		return descriptions;
	}

	private ConceptPojo createConceptPojo() {
		ConceptPojo pojo = new ConceptPojo();
		pojo.setActive(true);
		pojo.setModuleId("900000000000012004");
		pojo.setConceptId("123456");
		pojo.setDefinitionStatus(org.ihtsdo.otf.rest.client.snowowl.pojo.DefinitionStatus.FULLY_DEFINED);
		Set<DescriptionPojo> descriptions = createDescriptionPojos();
		pojo.setDescriptions(descriptions);
		Set<RelationshipPojo> relationships = createRelationshipPojos("123456");
		pojo.setRelationships(relationships);
		return pojo;
	}

	private Set<RelationshipPojo> createRelationshipPojos(String sourceId) {
		Set<RelationshipPojo> pojos = new HashSet<>();
		RelationshipPojo rel1 = new RelationshipPojo(0, "116680003", "654321", "STATED_RELATIONSHIP");
		rel1.setSourceId(sourceId);
		pojos.add(rel1);
		RelationshipPojo rel2 = new RelationshipPojo(0, "246075003", "6543217", "STATED_RELATIONSHIP");
		rel2.setSourceId(sourceId);
		pojos.add(rel2);
		return pojos;
	}

	private Set<DescriptionPojo> createDescriptionPojos() {
		Set<DescriptionPojo> pojos = new HashSet<>();
		DescriptionPojo pojo = new DescriptionPojo();
		pojo.setActive(true);
		pojo.setCaseSignificance("ci");
		pojo.setTerm("Allergy to eggs");
		pojo.setType(DescriptionType.SYNONYM.name());
		pojos.add(pojo);
		
		DescriptionPojo fsn = new DescriptionPojo();
		fsn.setActive(true);
		fsn.setCaseSignificance("ci");
		fsn.setTerm("Allergy to eggs (disorder)");
		fsn.setType(DescriptionType.FSN.name());
		pojos.add(fsn);
		return pojos;
	}

	private OngoingStubbing<SnowOwlRestClient> mockTerminologyServerClient() {
		return when(clientFactory.getClient()).thenReturn(terminologyServerClient);
	}
}
