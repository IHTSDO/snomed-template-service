package org.ihtsdo.otf.transformationandtemplate.service.template;


import java.util.Collections;
import java.util.Set;

import org.ihtsdo.otf.transformationandtemplate.service.template.TemplateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
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
