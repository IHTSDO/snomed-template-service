package org.ihtsdo.otf.authoringtemplate.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
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
		assertEquals(4, conceptToTransform.getRelationships().size());
		
		List<RelationshipPojo> inActiveRels = conceptToTransform.getRelationships()
				.stream()
				.filter(r -> !r.isActive())
				.collect(Collectors.toList());
		assertEquals(0, inActiveRels.size());
		
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
	
	@Test
	public void testRelationshipComparator() {
		TreeSet<RelationshipPojo> relationships = new TreeSet<>(RelationshipTransformer.getRelationshipPojoComparator());
		relationships.add(TestDataHelper.createRelationshipPojo(0, "1234"));
		relationships.add(TestDataHelper.createRelationshipPojo(0, null));
		relationships.add(TestDataHelper.createRelationshipPojo(1, "12345"));
		relationships.addAll(TestDataHelper.createRelationshipPojos("23566", true));
		relationships.addAll(TestDataHelper.createRelationshipPojos("23567", false));
		assertEquals(11, relationships.size());
		RelationshipPojo first = relationships.first();
		assertEquals(TestDataHelper.STATED_RELATIONSHIP, first.getCharacteristicType());
		assertTrue(first.getRelationshipId() == null);
		assertEquals(0, first.getGroupId());
		
		RelationshipPojo last = relationships.last();
		assertEquals(TestDataHelper.INFERRED_RELATIONSHIP, last.getCharacteristicType());
		assertTrue(last.getRelationshipId() == null);
		assertEquals(1, last.getGroupId());
	}
}
