package org.ihtsdo.otf.transformationandtemplate.service;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TemplateUtilTest {
	
	@Test
	public void testGetDescriptionFromFSN() {
		assertEquals("Environment or geographical location", 
				TemplateUtil.getDescriptionFromFSN("Environment or geographical location (environment / location)"));
	}

	
	@Test
	public void testInvalidFSNWithoutSpaceBeforeBracket() {
		assertEquals("Environment or geographical location", 
				TemplateUtil.getDescriptionFromFSN("Environment or geographical location(environment / location)"));
	}
	
	@Test
	public void testFSNWithTwoBrackets() {
		assertEquals("Environment or (geographical) location", 
				TemplateUtil.getDescriptionFromFSN("Environment or (geographical) location (environment / location)"));
	}
	
	@Test
	public void testGetSlots() {
		Set<String> results = TemplateUtil.getSlots(Collections.singleton("Allergy to $substance$ (disorder)"));
		assertEquals(1, results.size());
		assertEquals("substance", results.iterator().next());
	}
}
