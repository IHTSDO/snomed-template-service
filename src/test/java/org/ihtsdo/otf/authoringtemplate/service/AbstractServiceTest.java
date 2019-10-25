package org.ihtsdo.otf.authoringtemplate.service;

import java.io.IOException;

import org.assertj.core.util.Lists;
import org.ihtsdo.otf.authoringtemplate.Config;
import org.ihtsdo.otf.authoringtemplate.TestConfig;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.authoringtemplate.transform.TestDataHelper;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowOwlRestClient;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowOwlRestClientFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.Description;
import org.snomed.authoringtemplate.domain.DescriptionType;
import org.snomed.authoringtemplate.domain.LexicalTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileSystemUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, TestConfig.class})
public abstract class AbstractServiceTest {

	@Autowired
	protected TemplateService templateService;

	@Autowired
	protected TemplateStore templateStore;

	@MockBean
	protected SnowOwlRestClientFactory clientFactory;

	@MockBean
	protected SnowOwlRestClient terminologyServerClient;
	
	@Before
	public void before() {
		SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("", ""));
		templateStore.clear();
	}

	@After
	public void after() {
		// Recreate empty template store
		FileSystemUtils.deleteRecursively(templateStore.getJsonStore().getStoreDirectory());
		templateStore.getJsonStore().getStoreDirectory().mkdirs();
	}
	
	public void createCtGuidedProcedureOfX() throws IOException, ServiceException {
		final ConceptTemplate templateRequest = new ConceptTemplate();
		templateRequest.setDomain("<<71388002 |Procedure|");
		templateRequest.setLogicalTemplate("71388002 |Procedure|:   [[~1..1]] {      260686004 |Method| = 312251004 |Computed tomography imaging action|,      [[~1..1]] 405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure|) @procSite]],      363703001 |Has intent| = 429892002 |Guidance intent|   },   {      260686004 |Method| = [[+id (<< 129264002 |Action|) @action]],      [[~1..1]] 405813007 |Procedure site - Direct| = [[+id $procSite]]   }");

		templateRequest.addLexicalTemplate(new LexicalTemplate("procSiteTerm", "X", "procSite", Lists.newArrayList("structure of", "structure", "part of")));
		templateRequest.addLexicalTemplate(new LexicalTemplate("actionTerm", "Procedure", "action", Lists.newArrayList(" - action")));
		Description fsn = new Description("$actionTerm$ of $procSiteTerm$ using computed tomography guidance (procedure)");
		fsn.setType(DescriptionType.FSN);
		fsn.setAcceptabilityMap(TestDataHelper.constructAcceptabilityMap(Constants.PREFERRED, Constants.PREFERRED));
		Description pt = new Description("$actionTerm$ of $procSiteTerm$ using computed tomography guidance");
		pt.setType(DescriptionType.SYNONYM);
		pt.setAcceptabilityMap(TestDataHelper.constructAcceptabilityMap(Constants.PREFERRED, Constants.PREFERRED));
		templateRequest.setConceptOutline(new ConceptOutline().addDescription(fsn).addDescription(pt));
		templateService.create("CT Guided Procedure of X", templateRequest);
	}
}
