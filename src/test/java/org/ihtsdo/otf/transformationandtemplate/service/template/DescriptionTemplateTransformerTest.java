package org.ihtsdo.otf.transformationandtemplate.service.template;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.transformationandtemplate.service.TestDataHelper;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.DescriptionType;
import org.snomed.authoringtemplate.domain.LexicalTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.CaseSignificance.CASE_INSENSITIVE;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Type.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class DescriptionTemplateTransformerTest {

	@Test
	public void testTransform() throws ServiceException {
		ConceptPojo conceptToTransform = TestDataHelper.createConceptPojo();
		ConceptOutline conceptOutline = TestDataHelper.createConceptOutline();
		String inactivationReason = "OUTDATED";
		ConceptTemplate conceptTemplate = new ConceptTemplate();
		conceptTemplate.setConceptOutline(conceptOutline);
		LexicalTemplate lexical = new LexicalTemplate();
		lexical.setName("substance");
		lexical.setTakeFSNFromSlot("substance");
		conceptTemplate.addLexicalTemplate(lexical);
		Map<String, String> slotValueMap = new HashMap<>();
		slotValueMap.put("substance", "Almond");
		Map<String, Set<DescriptionPojo>> slotDescriptonValuesMap = TestDataHelper.constructSlotDescriptionValuesMap(slotValueMap, null, DescriptionType.FSN);
		DescriptionTemplateTransformer transformer = new DescriptionTemplateTransformer(conceptToTransform, conceptTemplate, slotDescriptonValuesMap, inactivationReason);
		transformer.transform();
		assertNotNull(conceptToTransform.getDescriptions());
		assertEquals(4, conceptToTransform.getDescriptions().size());
		List<DescriptionPojo> activeTerms = conceptToTransform.getDescriptions()
				.stream()
				.filter(DescriptionPojo::isActive)
				.collect(Collectors.toList());
		assertEquals(3,  activeTerms.size());
		for (DescriptionPojo pojo : activeTerms) {
			assertEquals(CASE_INSENSITIVE, pojo.getCaseSignificance());
			if (FSN == pojo.getType()) {
				assertEquals("Allergy to almond (finding)", pojo.getTerm());
			} else if (SYNONYM == pojo.getType()) {
				assertEquals("Allergy to almond", pojo.getTerm());
				assertTrue(pojo.isReleased());
			} else {
				assertEquals(TEXT_DEFINITION, pojo.getType());
				assertEquals("Allergy to almond text definition", pojo.getTerm());
				Set<DescriptionPojo.Acceptability> valueSet = new HashSet<>(pojo.getAcceptabilityMap().values());
				assertEquals(1, valueSet.size());
				assertEquals(DescriptionPojo.Acceptability.PREFERRED, valueSet.iterator().next());
				assertTrue(pojo.isReleased());
			}
		}
		
		List<DescriptionPojo> inactiveTerms = conceptToTransform.getDescriptions()
				.stream()
				.filter(d -> !d.isActive())
				.collect(Collectors.toList());
		assertEquals(1,  inactiveTerms.size());
		for (DescriptionPojo pojo : inactiveTerms) {
			assertEquals(FSN, pojo.getType());
			assertEquals("Allergy to almond (disorder)", pojo.getTerm());
			assertEquals(inactivationReason, pojo.getInactivationIndicator().toString());
		}
	}
}
