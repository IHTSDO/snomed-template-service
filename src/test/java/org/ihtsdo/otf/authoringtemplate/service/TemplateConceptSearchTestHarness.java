package org.ihtsdo.otf.authoringtemplate.service;

import org.ihtsdo.otf.authoringtemplate.Config;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class})
public class TemplateConceptSearchTestHarness {

	@Autowired
	private TemplateConceptSearchService templateSearchService;
	
	private static final String COOKIE = "uat-ims-ihtsdo=y0AmzS125Lhi9XEZJAkQQw00";
	
	@BeforeClass
	public static void setUp() {
		PreAuthenticatedAuthenticationToken decoratedAuthentication = new PreAuthenticatedAuthenticationToken("mchu", COOKIE);
		SecurityContextHolder.getContext().setAuthentication(decoratedAuthentication);
	}
	
	@Test
	public void testSearch() throws Exception {
		templateSearchService.searchConceptsByTemplate("Allergy to [substance] (finding)", "MAIN", true, false, true);
	}

}
