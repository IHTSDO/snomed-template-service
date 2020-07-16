package org.ihtsdo.otf.transformationandtemplate.service.template;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ihtsdo.otf.transformationandtemplate.service.ConstantStrings;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RelationshipPojo;

public class RoleGroupNumberGenerator {
	
	private Set<Integer> groupInUse;
	
	public RoleGroupNumberGenerator(List<List<RelationshipPojo>> relationshipGroups) {
		groupInUse = new HashSet<>();
		groupInUse.add(0);
		groupInUse.addAll(getGroupNumberInUse(relationshipGroups));
	}
	
	public Set<Integer> getGroupNumberInUse() {
		return this.groupInUse;
	}
	
	public int getRoleGroupNumber(List<RelationshipPojo> roleGroup) {
		int groupNum;
		Map<Integer, Integer> publishedGroupNumberCounterMap = getGroupNumberCounterMap(roleGroup, true);
		Map<Integer, Integer> newGroupNumberCounterMap = getGroupNumberCounterMap(roleGroup, false);
		Map.Entry<Integer, Integer> publishedMaxEntry = publishedGroupNumberCounterMap.entrySet().stream().max(Map.Entry.comparingByValue()).get();
		Map.Entry<Integer, Integer> newMaxEntry = newGroupNumberCounterMap.entrySet().stream().max(Map.Entry.comparingByKey()).get();
		
		if (publishedMaxEntry.getValue() >= 2 || publishedMaxEntry.getValue() >= newMaxEntry.getValue()) {
			groupNum = publishedMaxEntry.getKey();
			if (groupInUse.contains(groupNum) && groupNum > 0) {
				// Use existing published group
				return groupNum;
			}
		} else {
			groupNum = newMaxEntry.getKey();
		}
		
		if (groupNum == 0 && !containsISA(roleGroup)) {
			groupNum = 1;
		}
		if (groupNum != 0) {
			while (groupInUse.contains(groupNum)) {
				Integer max = groupInUse.stream().max(Comparator.comparing(Integer:: intValue)).get();
				groupNum = max + 1;
			}
		}
		groupInUse.add(groupNum);
		return groupNum;
	}
	
	private boolean containsISA(List<RelationshipPojo> roleGroup) {
		for (RelationshipPojo rel : roleGroup) {
			if (ConstantStrings.IS_A.equals(rel.getType().getConceptId())) {
				return true;
			}
		}
		return false;
	}
	
	private Set<Integer> getGroupNumberInUse(List<List<RelationshipPojo>> relationshipGroups) {
		Set<Integer> groupInUse = new HashSet<>();
		for (List<RelationshipPojo> rels : relationshipGroups) {
			Map<Integer, Integer> publishedGroupNumberCounterMap = getGroupNumberCounterMap(rels, true);
			Map.Entry<Integer, Integer> publishedMaxEntry = publishedGroupNumberCounterMap.entrySet().stream().max(Map.Entry.comparingByValue()).get();
			if (publishedMaxEntry.getValue() >=2) {
				groupInUse.add(publishedMaxEntry.getKey());
			}
		}
		return groupInUse;
	}
	
	private Map<Integer, Integer> getGroupNumberCounterMap (List<RelationshipPojo> roleGroup, boolean isPublished) {
		Map<Integer, Integer> groupNumberCounterMap = new HashMap<>();
		for (RelationshipPojo pojo : roleGroup) {
			Integer counter = groupNumberCounterMap.computeIfAbsent(pojo.getGroupId(), k -> 0);
			if (isPublished) {
				if (pojo.getRelationshipId() != null || pojo.isReleased()) {
					groupNumberCounterMap.put(pojo.getGroupId(), counter + 1);
				}
			} else {
				if (pojo.getRelationshipId() == null || !pojo.isReleased()) {
					groupNumberCounterMap.put(pojo.getGroupId(), counter + 1);
				}
			}
		}
		return groupNumberCounterMap;
	}
}
