package org.ihtsdo.otf.transformationandtemplate.service;

import org.snomed.authoringtemplate.domain.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Set;

public class JsonStoreTest extends AbstractServiceTest {

	@Autowired
	private JsonStore templateJsonStore;

	@Test
	public void testSaveLoad() throws Exception {
		Assert.assertNull(templateJsonStore.load("one", ConceptMini.class));
		templateJsonStore.save("one", new ConceptMini("123"));
		final ConceptMini one = templateJsonStore.load("one", ConceptMini.class);
		Assert.assertEquals("123", one.getConceptId());
	}

	@Test
	public void testLoadAll() throws Exception {
		templateJsonStore.save("one", new ConceptMini("100"));
		templateJsonStore.save("two", new ConceptMini("200"));
		templateJsonStore.save("three", new ConceptMini("300"));
		final Set<ConceptMini> conceptMinis = templateJsonStore.loadAll(ConceptMini.class);
		Assert.assertEquals(3, conceptMinis.size());
	}
	
	
	@Test
	public void testUriEncoding() {
		
		UriComponentsBuilder queryBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/snowstorm/snomed-ct/v2/MAIN/STORMTEST1/STORMTEST1-183/concepts")
				.queryParam("active", true)
				.queryParam("offset", 0)
				.queryParam("limit", 10);
		String ecl = "(<<420134006) AND (<<420134006:[1..1]{[1..1]719722006=472964009,[1..1]246075003=<105590001 |Substance (substance)|})";
		queryBuilder.queryParam("statedEcl", ecl);
		
		URI uri = queryBuilder.build().encode().toUri();
		System.out.println(uri);
		String expecedUrl ="http://localhost:8080/snowstorm/snomed-ct/v2/MAIN/STORMTEST1/STORMTEST1-183/concepts?active=true&offset=0&limit=10&statedEcl=(%3C%3C420134006)%20AND%20(%3C%3C420134006:%5B1..1%5D%7B%5B1..1%5D719722006%3D472964009,%5B1..1%5D246075003%3D%3C105590001%20%7CSubstance%20(substance)%7C%7D)";
		assertEquals(expecedUrl, uri.toString());
		String queryStr = uri.getQuery();
		System.out.println("queryString=" + queryStr);
		String expected = "active=true&offset=0&limit=10&statedEcl=(<<420134006) AND (<<420134006:[1..1]{[1..1]719722006=472964009,[1..1]246075003=<105590001 |Substance (substance)|})";

		assertEquals(expected, queryStr);
	}

}