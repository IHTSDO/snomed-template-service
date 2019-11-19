package org.ihtsdo.otf.authoringtemplate.transform;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ihtsdo.otf.authoringtemplate.service.Constants;
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
		int groupNum = -1;
		Map<Integer, Integer> publishedGroupNumberCounterMap = getGroupNumberCounterMap(roleGroup, true);
		Map<Integer, Integer> newGroupNumberCounterMap = getGroupNumberCounterMap(roleGroup, false);
		Map.Entry<Integer, Integer> publishedMaxEntry = publishedGroupNumberCounterMap.entrySet().stream().max(Map.Entry.comparingByValue()).get();
		Map.Entry<Integer, Integer> newMaxEntry = newGroupNumberCounterMap.entrySet().stream().max(Map.Entry.comparingByKey()).get();
		
		if (publishedMaxEntry.getValue() >=2 || publishedMaxEntry.getValue() >= newMaxEntry.getValue()) {
			groupNum = publishedMaxEntry.getKey().intValue();
			if (groupInUse.contains(groupNum) && groupNum > 0) {
				//use existing published group
				return groupNum;
			}
		} else {
			groupNum = newMaxEntry.getKey().intValue();
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
			if (Constants.IS_A.equals(rel.getType().getConceptId())) {
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
				groupInUse.add(publishedMaxEntry.getKey().intValue());
			}
		}
		return groupInUse;
	}
	
	private Map<Integer, Integer> getGroupNumberCounterMap (List<RelationshipPojo> roleGroup, boolean isPublished) {
		Map<Integer, Integer> groupNumberCounterMap = new HashMap<>();
		for (RelationshipPojo pojo : roleGroup) {
			Integer counter = groupNumberCounterMap.get(pojo.getGroupId());
			if (counter == null) {
				counter =  new Integer(0);
				groupNumberCounterMap.put(pojo.getGroupId(), counter);
			}
			if (isPublished) {
				if (pojo.getRelationshipId() != null || pojo.isReleased()) {
					groupNumberCounterMap.put(pojo.getGroupId(), new Integer(counter +1));
				}
			} else {
				if (pojo.getRelationshipId() == null || !pojo.isReleased()) {
					groupNumberCounterMap.put(pojo.getGroupId(), new Integer(counter +1));
				}
			  }
			}
		return groupNumberCounterMap;
	}
}
