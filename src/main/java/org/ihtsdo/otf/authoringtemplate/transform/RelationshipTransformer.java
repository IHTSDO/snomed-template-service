package org.ihtsdo.otf.authoringtemplate.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.ihtsdo.otf.authoringtemplate.domain.ConceptOutline;
import org.ihtsdo.otf.authoringtemplate.domain.Relationship;
import org.ihtsdo.otf.authoringtemplate.service.Constants;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptMiniPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.RelationshipPojo;

public class RelationshipTransformer {
	
	private ConceptPojo conceptToTransform;
	private ConceptOutline conceptOutline;
	private Map<String, ConceptMiniPojo> attributeSlotMap;
	private Map<String, String> conceptFsnMap;

	public RelationshipTransformer(ConceptPojo conceptToTransform, ConceptOutline conceptOutline,
			Map<String, ConceptMiniPojo> attributeSlotMap, Map<String, String> conceptFsnMap) {
		this.conceptToTransform = conceptToTransform;
		this.conceptOutline = conceptOutline;
		this.attributeSlotMap = attributeSlotMap;
		this.conceptFsnMap = conceptFsnMap;
	}

	public void tranform() {
		//map relationship by group
		List<RelationshipPojo> statedRels = conceptToTransform.getRelationships().stream()
				.filter(r -> r.getCharacteristicType().equals(Constants.STATED))
				.collect(Collectors.toList());
		
		Map<Integer, Map<String, RelationshipPojo>> existingRelGroupMap = new HashMap<>();
		for (RelationshipPojo pojo : statedRels) {
			if (existingRelGroupMap.get(pojo.getGroupId()) == null) {
				existingRelGroupMap.put(pojo.getGroupId(), new HashMap<>());
			}
			String key = pojo.getTarget().getConceptId() + pojo.getType().getConceptId();
			existingRelGroupMap.get(pojo.getGroupId()).put(key, pojo);
		}

		Map<Integer, Map<String, Relationship>> newRelGroupMap = new HashMap<>();
		for (Relationship rel : conceptOutline.getRelationships()) {
			if (newRelGroupMap.get(rel.getGroupId()) == null) {
				newRelGroupMap.put(rel.getGroupId(), new HashMap<>());
			}
			if (rel.getTarget() != null) {
				String key = rel.getTarget().getConceptId() + rel.getType().getConceptId();
				newRelGroupMap.get(rel.getGroupId()).put(key, rel);
			} else {
				//handle relationship with slot
				if (rel.getTargetSlot() != null) {
					String key = rel.getTargetSlot().getSlotName() + rel.getType().getConceptId();
					newRelGroupMap.get(rel.getGroupId()).put(key, rel);
				}
			}
		}
		
		Set<Set<RelationshipPojo>> megedSet = constructRelationshipSet(existingRelGroupMap, newRelGroupMap);
		Set<RelationshipPojo> relationships = new HashSet<>();
		for (Set<RelationshipPojo> roleGroup : megedSet ) {
			int roleGrp = getRoleGroup(roleGroup);
			for (RelationshipPojo pojo : roleGroup) {
				if (pojo.getGroupId() == roleGrp) {
					if (pojo.getRelationshipId() == null) {
						pojo.setSourceId(conceptToTransform.getConceptId());
					}
					relationships.add(pojo);
				}
			}
		}
		for (RelationshipPojo pojo : statedRels) {
			if (pojo.isActive() && !relationships.contains(pojo)) {
				pojo.setActive(false);
				relationships.add(pojo);
			}
		}
		List<RelationshipPojo> inferred = new ArrayList<>();
		inferred = conceptToTransform.getRelationships().stream()
				.filter(r -> r.getCharacteristicType().equals(Constants.INFERRED))
				.collect(Collectors.toList());
		relationships.addAll(inferred);
		conceptToTransform.setRelationships(relationships);
	}
	
	private int getRoleGroup(Set<RelationshipPojo> roleGroup) {
		int roleGrp = 0;
		for (RelationshipPojo rel : roleGroup) {
			if (rel.getRelationshipId() != null) {
				if (roleGrp == rel.getGroupId()) {
					return roleGrp;
				}
				roleGrp = rel.getGroupId();
			} 
		}
		
		for (RelationshipPojo rel : roleGroup) {
			if (rel.getRelationshipId() == null) {
				if (roleGrp == rel.getGroupId()) {
					return roleGrp;
				}
				roleGrp = rel.getGroupId();
			} 
		}
		return roleGrp;
	}

	private Set<Set<RelationshipPojo>> constructRelationshipSet(Map<Integer, Map<String, RelationshipPojo>> existingRelGroupMap,
			Map<Integer, Map<String, Relationship>> newRelGroupMap) {
		Set<Set<String>> existingSet = new HashSet<>();
		existingRelGroupMap.values().stream().forEach(r -> existingSet.add(r.keySet()));
		Map<String, Set<RelationshipPojo>> relMapByTargetAndType = new HashMap<>();
		for (Map<String, RelationshipPojo> relMap : existingRelGroupMap.values()) {
			for (String key : relMap.keySet()) {
				if (!relMapByTargetAndType.containsKey(key)) {
					relMapByTargetAndType.put(key, new HashSet<>());
				}
				relMapByTargetAndType.get(key).add(relMap.get(key));
			}
		}
		
		Set<Set<String>> newRelSet = new HashSet<>();
		newRelGroupMap.values().stream().forEach(r -> newRelSet.add(r.keySet()));
		
		
		Map<String, Set<Relationship>> newRelMapByTargetAndType = new HashMap<>();
		for (Map<String, Relationship> relMap : newRelGroupMap.values()) {
			for (String key : relMap.keySet()) {
				if (!newRelMapByTargetAndType.containsKey(key)) {
					newRelMapByTargetAndType.put(key, new HashSet<>());
				}
				newRelMapByTargetAndType.get(key).add(relMap.get(key));
			}
		}
		
		Set<Set<RelationshipPojo>> mergedSet = new HashSet<>();
		//match existing role groups
		for (Set<String> relSet : newRelSet) {
			Set<RelationshipPojo> pojoSet = new HashSet<>();
			for (String key : relSet) {
				if (relMapByTargetAndType.containsKey(key)) {
					pojoSet.addAll(relMapByTargetAndType.get(key));
				} else {
					Relationship relationship = newRelMapByTargetAndType.get(key).iterator().next();
					pojoSet.add(constructRelationshipPojo(relationship, attributeSlotMap));
				}
			}
			mergedSet.add(pojoSet);
		}
		return mergedSet;
	}

	private RelationshipPojo constructRelationshipPojo(Relationship relationship, Map<String, ConceptMiniPojo> attributeSlotMap) {
		RelationshipPojo pojo = new RelationshipPojo();
		pojo.setActive(true);
		pojo.setCharacteristicType(relationship.getCharacteristicType());
		pojo.setGroupId(relationship.getGroupId());
		pojo.setModifier(Constants.EXISTENTIAL);
		if (relationship.getTarget() != null) {
			pojo.setTarget(constructConceptMiniPojo(relationship.getTarget().getConceptId()));
		} else {
			if (relationship.getTargetSlot() != null) {
				pojo.setTarget(attributeSlotMap.get(relationship.getTargetSlot()));
			} 
		}
		pojo.setType(constructConceptMiniPojo(relationship.getType().getConceptId()));
		return pojo;
	}
	
	private ConceptMiniPojo constructConceptMiniPojo(String conceptId) {
		ConceptMiniPojo pojo = new ConceptMiniPojo();
		if (conceptId != null) {
			pojo.setConceptId(conceptId);
			pojo.setFsn(conceptFsnMap.get(conceptId));
		}
		return pojo;
	}
}
