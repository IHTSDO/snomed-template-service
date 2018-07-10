package org.ihtsdo.otf.authoringtemplate.transform;

import static org.ihtsdo.otf.authoringtemplate.transform.TestDataHelper.createRelationshipPojo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.ihtsdo.otf.rest.client.snowowl.pojo.RelationshipPojo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RoleGroupNumberGeneratorTest {

	@Test
	public void testDefaultGroupNumberInUse() {
		RoleGroupNumberGenerator generator = new RoleGroupNumberGenerator(Collections.emptyList());
		Set<Integer> groupNumberInUse = generator.getGroupNumberInUse();
		assertNotNull(groupNumberInUse);
		assertEquals(1, groupNumberInUse.size());
		assertEquals(0, groupNumberInUse.iterator().next().intValue());
	}
	
	@Test
	public void testMergeExistingUngroupedRels() {
		RoleGroupNumberGenerator generator = new RoleGroupNumberGenerator(Collections.emptyList());
		List<RelationshipPojo> roleGroup = new ArrayList<>();
		roleGroup.add(createRelationshipPojo(0, "123456"));
		roleGroup.add(createRelationshipPojo(0, "123457"));
		roleGroup.add(createRelationshipPojo(1, null));
		int nextGrpNumber = generator.getRoleGroupNumber(roleGroup);
		assertEquals(1, nextGrpNumber);
	}
	
	
	@Test
	public void testGroupZero() {
		RoleGroupNumberGenerator generator = new RoleGroupNumberGenerator(Collections.emptyList());
		List<RelationshipPojo> roleGroup = new ArrayList<>();
		roleGroup.add(createRelationshipPojo(0, "123456"));
		roleGroup.add(createRelationshipPojo(0, "123457"));
		roleGroup.add(createRelationshipPojo(0, "116680003", null));
		int nextGrpNumber = generator.getRoleGroupNumber(roleGroup);
		assertEquals(0, nextGrpNumber);
	}
	
	
	@Test
	public void testWhenExistingRolesWith2InOneGroup() {
		RoleGroupNumberGenerator generator = new RoleGroupNumberGenerator(Collections.emptyList());
		List<RelationshipPojo> roleGroup = new ArrayList<>();
		roleGroup.add(createRelationshipPojo(0, "123456"));
		roleGroup.add(createRelationshipPojo(1, "123457"));
		roleGroup.add(createRelationshipPojo(1, "123458"));
		roleGroup.add(createRelationshipPojo(2, null));
		roleGroup.add(createRelationshipPojo(2, null));
		roleGroup.add(createRelationshipPojo(2, null));
		int nextGrpNumber = generator.getRoleGroupNumber(roleGroup);
		assertEquals(1, nextGrpNumber);
	}
	
	@Test
	public void testWhenExistingGroupHasMoreRels() {
		RoleGroupNumberGenerator generator = new RoleGroupNumberGenerator(Collections.emptyList());
		List<RelationshipPojo> roleGroup = new ArrayList<>();
		roleGroup.add(createRelationshipPojo(1, "123456"));
		roleGroup.add(createRelationshipPojo(2, "123457"));
		roleGroup.add(createRelationshipPojo(3, "123458"));
		roleGroup.add(createRelationshipPojo(3, null));
		int nextGrpNumber = generator.getRoleGroupNumber(roleGroup);
		assertEquals(1, nextGrpNumber);
	}
	

	@Test
	public void testWhenNewGroupHasMore() {
		RoleGroupNumberGenerator generator = new RoleGroupNumberGenerator(Collections.emptyList());
		List<RelationshipPojo> roleGroup = new ArrayList<>();
		roleGroup.add(createRelationshipPojo(0, "123456"));
		roleGroup.add(createRelationshipPojo(1, "123457"));
		roleGroup.add(createRelationshipPojo(2, "123458"));
		roleGroup.add(createRelationshipPojo(3, null));
		roleGroup.add(createRelationshipPojo(3, null));
		int nextGrpNumber = generator.getRoleGroupNumber(roleGroup);
		assertEquals(3, nextGrpNumber);
		assertEquals(4, generator.getRoleGroupNumber(roleGroup));
	}
	
	@Test
	public void testWhenGroupIsAlreadyInUse() {
		RoleGroupNumberGenerator generator = new RoleGroupNumberGenerator(Collections.emptyList());
		List<RelationshipPojo> roleGroup = new ArrayList<>();
		roleGroup.add(createRelationshipPojo(0, "123456"));
		roleGroup.add(createRelationshipPojo(1, "123457"));
		roleGroup.add(createRelationshipPojo(2, "123458"));
		roleGroup.add(createRelationshipPojo(3, null));
		roleGroup.add(createRelationshipPojo(3, null));
		int nextGrpNumber = generator.getRoleGroupNumber(roleGroup);
		assertEquals(3, nextGrpNumber);
		Set<Integer> groupNumberInUse = generator.getGroupNumberInUse();
		assertNotNull(groupNumberInUse );
		assertEquals(2, groupNumberInUse.size());
		assertEquals(4, generator.getRoleGroupNumber(roleGroup));
		
	}
}
