package org.ihtsdo.otf.authoringtemplate.transform.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
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
import org.ihtsdo.otf.authoringtemplate.service.JsonStore;
import org.ihtsdo.otf.authoringtemplate.service.TemplateService;
import org.ihtsdo.otf.authoringtemplate.transform.TemplateTransformRequest;
import org.ihtsdo.otf.authoringtemplate.transform.TemplateTransformation;
import org.ihtsdo.otf.authoringtemplate.transform.TransformationResult;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClient;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.RelationshipPojo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, TestConfig.class})
public class TemplateConceptTransformationTestHarness {
		
		private static final String TEMPLATES_DIR = "/Users/mchu/Development/snomed-templates/";

		private static final String JSON = ".json";
		
		@Autowired
		private TemplateConceptTransformService transformService;
		
		private SnowOwlRestClient termServerRestClient;
		
		@Autowired
		private TemplateService templateService;

		@Autowired
		private JsonStore jsonStore;
		
		private String source;
		
		private String destination;
		
		@Before
		public void setUp() throws Exception {
			source = "[Clinical course] contact dermatitis of [body structure] caused by [substance] v1 - OUTDATED";
			FileUtils.copyFileToDirectory(new File(TEMPLATES_DIR + source + JSON),
					jsonStore.getStoreDirectory());
			destination = "[Clinical course] contact dermatitis of [body structure] caused by [substance] (disorder) v2";
			FileUtils.copyFileToDirectory(new File(TEMPLATES_DIR + destination + JSON),
					jsonStore.getStoreDirectory());
			templateService.reloadCache();
			String snowOwlUrl = "https://dev-authoring.ihtsdotools.org/snowowl/";
			String singleSignOnCookie = "dev-ims-ihtsdo=As8jOHP8h7UmM9rByqJk3Q00";
			termServerRestClient = new SnowOwlRestClient(snowOwlUrl, singleSignOnCookie);
		}
		
		@Test
		public void testConceptTransformation() throws Exception {
			Set<String> concepts = new HashSet<>();
			concepts.add("40275004");
			
			TemplateTransformRequest transformRequest = new TemplateTransformRequest();
			transformRequest.setConceptsToTransform(concepts);
			transformRequest.setSourceTemplate(source);
			TemplateTransformation transformation = new TemplateTransformation("MAIN", destination, transformRequest);
			List<Future<TransformationResult>> results = transformService.transform(transformation, termServerRestClient);
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
			List<RelationshipPojo> stated = concept.getRelationships()
					.stream().filter(r -> r.getCharacteristicType().equals("STATED_RELATIONSHIP"))
					.collect(Collectors.toList());
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
}
