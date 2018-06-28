package org.ihtsdo.otf.authoringtemplate.transform;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.ihtsdo.otf.authoringtemplate.domain.ConceptOutline;
import org.ihtsdo.otf.authoringtemplate.domain.Relationship;
import org.ihtsdo.otf.authoringtemplate.service.Constants;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptMiniPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.RelationshipPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.SimpleConceptPojo;

public class RelationshipTransformer {
	
	private ConceptPojo conceptToTransform;
	private ConceptOutline conceptOutline;
	private Map<String, ConceptMiniPojo> attributeSlotMap;
	private Map<String, SimpleConceptPojo> conceptIdMap;

	public RelationshipTransformer(ConceptPojo conceptToTransform, ConceptOutline conceptOutline,
			Map<String, ConceptMiniPojo> attributeSlotMap, Map<String, SimpleConceptPojo> conceptIdMap) {
		this.conceptToTransform = conceptToTransform;
		this.conceptOutline = conceptOutline;
		this.attributeSlotMap = attributeSlotMap;
		this.conceptIdMap = conceptIdMap;
	}

	public void transform() throws ServiceException {
		//map relationship by group
		List<RelationshipPojo> statedRels = conceptToTransform.getRelationships().stream()
				.filter(r -> r.getCharacteristicType().equals(Constants.STATED))
				.collect(Collectors.toList());
		
		Map<Integer, Map<String, RelationshipPojo>> existingRelGroupMap = new HashMap<>();
		for (RelationshipPojo pojo : statedRels) {
			if (existingRelGroupMap.get(pojo.getGroupId()) == null) {
				existingRelGroupMap.put(pojo.getGroupId(), new HashMap<>());
			}
			String key = pojo.getTarget().getConceptId() + "_" +  pojo.getType().getConceptId();
			existingRelGroupMap.get(pojo.getGroupId()).put(key, pojo);
		}

		Map<Integer, Map<String, Relationship>> newRelGroupMap = new HashMap<>();
		for (Relationship rel : conceptOutline.getRelationships()) {
			if (newRelGroupMap.get(rel.getGroupId()) == null) {
				newRelGroupMap.put(rel.getGroupId(), new HashMap<>());
			}
			if (rel.getTarget() != null) {
				String key = rel.getTarget().getConceptId() + "_"  + rel.getType().getConceptId();
				newRelGroupMap.get(rel.getGroupId()).put(key, rel);
			} else {
				//handle relationship with slot
				if (rel.getTargetSlot() != null) {
					String slot = rel.getTargetSlot().getSlotName();
					ConceptMiniPojo target = attributeSlotMap.get(slot);
					if (target == null) {
						throw new ServiceException(" Fail to find attribute slot value " + slot);
					}
					String key = target.getConceptId() + "_" + rel.getType().getConceptId();
					newRelGroupMap.get(rel.getGroupId()).put(key, rel);
				}
			}
		}
		
		Set<Set<RelationshipPojo>> megedSet = constructRelationshipSet(existingRelGroupMap, newRelGroupMap);
		List<RelationshipPojo> relationships = new ArrayList<>();
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
			if (!relationships.contains(pojo)) {
				if (pojo.isActive()) {
					pojo.setActive(false);
					pojo.setEffectiveTime(null);
				}
				relationships.add(pojo);
			}
		}
		List<RelationshipPojo> inferred = new ArrayList<>();
		inferred = conceptToTransform.getRelationships().stream()
				.filter(r -> r.getCharacteristicType().equals(Constants.INFERRED))
				.collect(Collectors.toList());
		relationships.addAll(inferred);
		Set<RelationshipPojo> rels = new TreeSet<RelationshipPojo>( new RelationshipPojoComparator());
		rels.addAll(relationships);
		conceptToTransform.setRelationships(rels);
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
			Map<Integer, Map<String, Relationship>> newRelGroupMap) throws ServiceException {
		Set<Set<String>> existingSet = new HashSet<>();
		existingRelGroupMap.values().stream().forEach(r -> existingSet.add(r.keySet()));
		Map<String, Set<RelationshipPojo>> relMapByTargetAndType = new HashMap<>();
		for (Map<String, RelationshipPojo> relMap : existingRelGroupMap.values()) {
			for (String key : relMap.keySet()) {
				if (!relMapByTargetAndType.containsKey(key)) {
					relMapByTargetAndType.put(key, new HashSet<RelationshipPojo>());
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
					newRelMapByTargetAndType.put(key, new HashSet<Relationship>());
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
			for (RelationshipPojo pojo : pojoSet) {
				//make sure relationship is active and effectiveTime is not set
				pojo.setActive(true);
			}
			mergedSet.add(pojoSet);
		}
		return mergedSet;
	}

	private RelationshipPojo constructRelationshipPojo(Relationship relationship, Map<String, ConceptMiniPojo> attributeSlotMap) throws ServiceException {
		RelationshipPojo pojo = new RelationshipPojo();
		pojo.setActive(true);
		pojo.setCharacteristicType(relationship.getCharacteristicType());
		pojo.setGroupId(relationship.getGroupId());
		pojo.setModifier(Constants.EXISTENTIAL);
		if (relationship.getTarget() != null) {
			pojo.setTarget(constructConceptMiniPojo(relationship.getTarget().getConceptId()));
		} else {
			if (relationship.getTargetSlot() != null) {
				ConceptMiniPojo target = attributeSlotMap.get(relationship.getTargetSlot().getSlotName());
				if (target == null) {
					throw new ServiceException(" Fail to find attribute slot value " + relationship.getTargetSlot().getSlotName());
				}
				pojo.setTarget(target);
			} 
		}
		pojo.setType(constructConceptMiniPojo(relationship.getType().getConceptId()));
		return pojo;
	}
	
	private ConceptMiniPojo constructConceptMiniPojo(String conceptId) {
		if (conceptId == null || conceptId.isEmpty()) {
			throw new IllegalArgumentException("Concept id can't be null or empty");
		}
		ConceptMiniPojo miniPojo = new ConceptMiniPojo();
		miniPojo.setConceptId(conceptId);
		if (conceptIdMap.get(conceptId) != null) {
			SimpleConceptPojo concept = conceptIdMap.get(conceptId);
			miniPojo.setFsn(concept.getFsn().getTerm());
			miniPojo.setModuleId(concept.getModuleId());
			miniPojo.setDefinitionStatus(concept.getDefinitionStatus());
		}
		return miniPojo;
	}
	
	private static class RelationshipPojoComparator implements Comparator<RelationshipPojo> {

		@Override
		public int compare(RelationshipPojo r1, RelationshipPojo r2) {
			if (r1.getSourceId().equals(r2.getSourceId())) {
				if (!r1.getCharacteristicType().equals(r2.getCharacteristicType())) {
					return  r1.getCharacteristicType().compareTo(r2.getCharacteristicType());
				}
				if (r1.getGroupId() != r2.getGroupId()) {
					return String.valueOf(r1.getGroupId()).compareTo(String.valueOf(r2.getGroupId()));
				}
				if (!r1.getTarget().getConceptId().equals(r2.getTarget().getConceptId())) {
					return r1.getTarget().getConceptId().compareTo(r2.getTarget().getConceptId());
				}
				if (!r1.getType().getConceptId().equals(r2.getType().getConceptId())) {
					return r1.getType().getConceptId().compareTo(r2.getType().getConceptId());
				}
				
				return Boolean.valueOf(r1.isActive()).compareTo(Boolean.valueOf(r1.isActive()));
			} else {
				return r1.getSourceId().compareTo(r2.getSourceId());
			}
		}
	}
}
