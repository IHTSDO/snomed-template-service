package org.ihtsdo.otf.authoringtemplate.service;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.ihtsdo.otf.authoringtemplate.Config;
import org.ihtsdo.otf.authoringtemplate.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, TestConfig.class})
public class TemplateConceptSearchServiceTestHarness {
		
		private static final String TEMPLATES_DIR = "/Users/mchu/Development/snomed-templates/";

		private static final String JSON = ".json";
		
		@Autowired
		private TemplateConceptSearchService searchService;
		
		@Autowired
		private TemplateService templateService;

		@Autowired
		private JsonStore jsonStore;
		
		private String source;

		
		@Before
		public void setUp() throws Exception {
			source = "[Clinical course] contact dermatitis of [body structure] caused by [substance] v1 - OUTDATED";
			FileUtils.copyFileToDirectory(new File(TEMPLATES_DIR + source + JSON),
					jsonStore.getStoreDirectory());
			templateService.reloadCache();
			String singleSignOnCookie = "Add_token_here";
			AbstractAuthenticationToken token = new PreAuthenticatedAuthenticationToken("", singleSignOnCookie);
			SecurityContextHolder.getContext().setAuthentication(token);
		}
		
		@Test
		public void testTemplateConceptSearch() throws Exception {
			searchService.searchConceptsByTemplate(source, "MAIN", true, false, false);
		}
}
