package org.ihtsdo.otf.authoringtemplate.transform;

import static org.ihtsdo.otf.authoringtemplate.service.Constants.PREFERRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.DescriptionPojo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.DescriptionType;
import org.snomed.authoringtemplate.domain.LexicalTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class DescriptionTransformerTest {

	@Test
	public void testTransform() throws ServiceException {
		ConceptPojo conceptToTransform = TestDataHelper.createConceptPojo();
		ConceptOutline conceptOutline = TestDataHelper.createConceptOutline();
		String inactivationReason = "Out_Of_Dated";
		ConceptTemplate conceptTempalte = new ConceptTemplate();
		conceptTempalte.setConceptOutline(conceptOutline);
		LexicalTemplate lexical = new LexicalTemplate();
		lexical.setName("substance");
		lexical.setTakeFSNFromSlot("substance");
		conceptTempalte.addLexicalTemplate(lexical);
		Map<String, String> slotValueMap = new HashMap<>();
		slotValueMap.put("substance", "Almond");
		Map<String, Set<DescriptionPojo>> slotDescriptonValuesMap = TestDataHelper.constructSlotDescriptionValuesMap(slotValueMap, null, DescriptionType.FSN);
		DescriptionTransformer transformer = new DescriptionTransformer(conceptToTransform, conceptTempalte, slotDescriptonValuesMap, inactivationReason);
		transformer.transform();
		assertNotNull(conceptToTransform.getDescriptions());
		assertEquals(4, conceptToTransform.getDescriptions().size());
		List<DescriptionPojo> activeTerms = conceptToTransform.getDescriptions()
				.stream()
				.filter(d -> d.isActive())
				.collect(Collectors.toList());
		assertEquals(3,  activeTerms.size());
		for (DescriptionPojo pojo : activeTerms) {
			assertEquals("CASE_INSENSITIVE", pojo.getCaseSignificance());
			if (DescriptionType.FSN.name().equals(pojo.getType())) {
				assertEquals("Allergy to almond (finding)", pojo.getTerm());
			} else if (DescriptionType.SYNONYM.name().equals(pojo.getType())) {
				assertEquals("Allergy to almond", pojo.getTerm());
				assertTrue(pojo.isReleased());
			} else {
				assertEquals("TEXT_DEFINITION", pojo.getType());
				assertEquals("Allergy to almond text definition", pojo.getTerm());
				Set<String> valueSet = pojo.getAcceptabilityMap().values().stream().collect(Collectors.toSet());
				assertEquals(1, valueSet.size());
				assertEquals(PREFERRED, valueSet.iterator().next());
				assertTrue(pojo.isReleased());
			}
		}
		
		List<DescriptionPojo> inactiveTerms = conceptToTransform.getDescriptions()
				.stream()
				.filter(d -> !d.isActive())
				.collect(Collectors.toList());
		assertEquals(1,  inactiveTerms.size());
		for (DescriptionPojo pojo : inactiveTerms) {
			assertEquals(DescriptionType.FSN.name(), pojo.getType());
			assertEquals("Allergy to almond (disorder)", pojo.getTerm());
			assertEquals(inactivationReason, pojo.getInactivationIndicator());
		}
	}
}
