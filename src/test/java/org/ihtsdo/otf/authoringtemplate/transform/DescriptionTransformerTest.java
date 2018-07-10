package org.ihtsdo.otf.authoringtemplate.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.DescriptionPojo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.DescriptionType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class DescriptionTransformerTest {

	private DescriptionTransformer transformer;
	
	@Test
	public void testTransform() {
		ConceptPojo conceptToTransform = TestDataHelper.createConceptPojo();
		ConceptOutline conceptOutline = TestDataHelper.createConceptOutline();
		Map<String, String> slotValueMap = new HashMap<>();
		String inactivationReason = "Out_Of_Dated";
		transformer = new DescriptionTransformer(conceptToTransform, conceptOutline, slotValueMap, inactivationReason);
		transformer.transform();
		assertNotNull(conceptToTransform.getDescriptions());
		assertEquals(3, conceptToTransform.getDescriptions().size());
		List<DescriptionPojo> activeTerms = conceptToTransform.getDescriptions()
				.stream()
				.filter(d -> d.isActive())
				.collect(Collectors.toList());
		assertEquals(2,  activeTerms.size());
		for (DescriptionPojo pojo : activeTerms) {
			if (DescriptionType.FSN.name().equals(pojo.getType())) {
				assertEquals("Allergy to almond (finding)", pojo.getTerm());
			} else {
				assertEquals("Allergy to almond", pojo.getTerm());
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
		}
	}
}
