package org.ihtsdo.otf.authoringtemplate.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.ihtsdo.otf.authoringtemplate.Config;
import org.ihtsdo.otf.authoringtemplate.TestConfig;
import org.ihtsdo.otf.authoringtemplate.domain.CaseSignificance;
import org.ihtsdo.otf.authoringtemplate.domain.ConceptMini;
import org.ihtsdo.otf.authoringtemplate.domain.ConceptOutline;
import org.ihtsdo.otf.authoringtemplate.domain.ConceptTemplate;
import org.ihtsdo.otf.authoringtemplate.domain.DefinitionStatus;
import org.ihtsdo.otf.authoringtemplate.domain.Description;
import org.ihtsdo.otf.authoringtemplate.domain.DescriptionType;
import org.ihtsdo.otf.authoringtemplate.domain.Relationship;
import org.ihtsdo.otf.authoringtemplate.domain.TemplateTransformation;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.authoringtemplate.transform.TemplateTransformRequest;
import org.ihtsdo.otf.authoringtemplate.transform.TransformationResult;
import org.ihtsdo.otf.authoringtemplate.transform.service.ConceptTemplateTransformService;
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
	public void testConceptTransform() throws Exception {
		Set<String> concepts = new HashSet<>();
		concepts.add("123456");
		
		mockTerminologyServerClient();
		ConceptPojo conceptPojo = createConceptPojo();
		when(terminologyServerClient.getConcept(anyString(), anyString()))
		.thenReturn(conceptPojo);
		
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
				transformed.addAll(transformationResult.getTransformedConcepts());
				for (String key : transformationResult.getErrors().keySet()) {
					errorMsgMap.put(key, transformationResult.getErrors().get(key));
				}
			} catch (InterruptedException | ExecutionException e) {
				fail("No exceptions should be thrown");
			}
		}
		assertEquals(true, !transformed.isEmpty());
		Gson gson =  new GsonBuilder().setPrettyPrinting().create();
		for (ConceptPojo pojo : transformed) {
			System.out.println(gson.toJson(pojo));
		}
		assertEquals(1, transformed.size());
		assertEquals(4, transformed.get(0).getRelationships().size());
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
		Set<RelationshipPojo> relationships = createRelationshipPojos();
		pojo.setRelationships(relationships);
		return pojo;
	}

	private Set<RelationshipPojo> createRelationshipPojos() {
		Set<RelationshipPojo> pojos = new HashSet<>();
		RelationshipPojo rel = new RelationshipPojo(0, "116680003", "654321", "900000000000010007");
		pojos.add(rel);
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
