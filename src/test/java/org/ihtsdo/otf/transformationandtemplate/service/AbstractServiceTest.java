package org.ihtsdo.otf.transformationandtemplate.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.assertj.core.util.Lists;
import org.ihtsdo.otf.transformationandtemplate.AbstractTest;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.transformationandtemplate.service.template.TemplateService;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowstormRestClient;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowstormRestClientFactory;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.Description;
import org.snomed.authoringtemplate.domain.DescriptionType;
import org.snomed.authoringtemplate.domain.LexicalTemplate;
import org.snomed.authoringtemplate.domain.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public abstract class AbstractServiceTest extends AbstractTest {

	@Autowired
	protected TemplateService templateService;

	@MockBean
	protected SnowstormRestClientFactory clientFactory;

	@MockBean
	protected SnowstormRestClient terminologyServerClient;
	
	public void createCtGuidedProcedureOfX() throws IOException, ServiceException {
		final ConceptTemplate templateRequest = new ConceptTemplate();
		templateRequest.setDomain("<<71388002 |Procedure|");
		templateRequest.setLogicalTemplate("71388002 |Procedure|:   [[~1..1]] {      260686004 |Method| = 312251004 |Computed tomography imaging action|,      [[~1..1]] 405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure|) @procSite]],      363703001 |Has intent| = 429892002 |Guidance intent|   },   {      260686004 |Method| = [[+id (<< 129264002 |Action|) @action]],      [[~1..1]] 405813007 |Procedure site - Direct| = [[+id $procSite]]   }");

		templateRequest.addLexicalTemplate(new LexicalTemplate("procSiteTerm", "X", "procSite", Lists.newArrayList("structure of", "structure", "part of")));
		templateRequest.addLexicalTemplate(new LexicalTemplate("actionTerm", "Procedure", "action", Lists.newArrayList(" - action")));
		Description fsn = new Description("$actionTerm$ of $procSiteTerm$ using computed tomography guidance (procedure)");
		fsn.setType(DescriptionType.FSN);
		fsn.setAcceptabilityMap(TestDataHelper.constructAcceptabilityMapStrings(ConstantStrings.PREFERRED, ConstantStrings.PREFERRED));
		Description pt = new Description("$actionTerm$ of $procSiteTerm$ using computed tomography guidance");
		pt.setType(DescriptionType.SYNONYM);
		pt.setAcceptabilityMap(TestDataHelper.constructAcceptabilityMapStrings(ConstantStrings.PREFERRED, ConstantStrings.PREFERRED));
		templateRequest.setConceptOutline(new ConceptOutline().addDescription(fsn).addDescription(pt));
		templateService.create("CT Guided Procedure of X", templateRequest);
	}
	
	public List<Relationship> getRelationships(ConceptOutline conceptOutline) {
		if (conceptOutline.getClassAxioms() == null || conceptOutline.getClassAxioms().isEmpty()) {
			return Collections.emptyList();
		}
		return conceptOutline.getClassAxioms().iterator().next().getRelationships();
	}
}
