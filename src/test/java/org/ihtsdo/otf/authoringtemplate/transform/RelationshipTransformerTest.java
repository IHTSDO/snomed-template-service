package org.ihtsdo.otf.authoringtemplate.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptMiniPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.RelationshipPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.SimpleConceptPojo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class RelationshipTransformerTest {
	
private RelationshipTransformer transformer;
	
	@Test
	public void testTransform() throws ServiceException {
		ConceptPojo conceptToTransform = TestDataHelper.createConceptPojo();
		ConceptOutline conceptOutline = TestDataHelper.createConceptOutline();
		Map<String, ConceptMiniPojo> attributeSlotMap = new HashMap<>();
		attributeSlotMap.put("substance", new ConceptMiniPojo("256350002"));
		Map<String, SimpleConceptPojo> conceptIdMap = new HashMap<>();
		transformer = new RelationshipTransformer(conceptToTransform, conceptOutline, attributeSlotMap, conceptIdMap);
		transformer.transform();
		assertNotNull(conceptToTransform.getRelationships());
		assertEquals(8, conceptToTransform.getRelationships().size());
		
		List<RelationshipPojo> inActiveRels = conceptToTransform.getRelationships()
				.stream()
				.filter(r -> !r.isActive())
				.collect(Collectors.toList());
		assertEquals(4, inActiveRels.size());
		
		List<RelationshipPojo> activeRels = conceptToTransform.getRelationships()
				.stream()
				.filter(r -> r.isActive())
				.collect(Collectors.toList());
		assertEquals(4, activeRels.size());
		
		List<RelationshipPojo> group0Rels = activeRels
				.stream()
				.filter(r -> r.getGroupId() == 0)
				.collect(Collectors.toList());
		assertEquals(2, group0Rels.size());
		
		List<RelationshipPojo> group1Rels = activeRels
				.stream()
				.filter(r -> r.getGroupId() == 1)
				.collect(Collectors.toList());
		assertEquals(2, group1Rels.size());
	}
}
