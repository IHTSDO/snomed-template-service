package org.ihtsdo.otf.authoringtemplate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ihtsdo.otf.authoringtemplate.App;
import org.ihtsdo.otf.authoringtemplate.TestConfig;
import org.ihtsdo.otf.authoringtemplate.domain.ConceptMini;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {App.class, TestConfig.class})
public class JsonStoreTest {

	@Autowired
	private JsonStore jsonStore;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void testSaveLoad() throws Exception {
		Assert.assertNull(jsonStore.load("one", ConceptMini.class));
		jsonStore.save("one", new ConceptMini("123"));
		final ConceptMini one = jsonStore.load("one", ConceptMini.class);
		Assert.assertEquals("123", one.getConceptId());
	}

	@Test
	public void testLoadAll() throws Exception {
		jsonStore.save("one", new ConceptMini("100"));
		jsonStore.save("two", new ConceptMini("200"));
		jsonStore.save("three", new ConceptMini("300"));
		final Set<ConceptMini> conceptMinis = jsonStore.loadAll(ConceptMini.class);
		Assert.assertEquals(3, conceptMinis.size());
	}
}