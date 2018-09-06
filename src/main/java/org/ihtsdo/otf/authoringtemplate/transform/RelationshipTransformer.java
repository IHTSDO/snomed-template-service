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

import org.ihtsdo.otf.authoringtemplate.service.Constants;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptMiniPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.RelationshipPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.SimpleConceptPojo;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.Relationship;

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
			if (pojo.isActive()) {
				existingRelGroupMap.computeIfAbsent(pojo.getGroupId(), k -> new HashMap<>())
				.put(pojo.getTarget().getConceptId() + "_" +  pojo.getType().getConceptId(), pojo);
			}
		}

		Map<Integer, Map<String, Relationship>> newRelGroupMap = new HashMap<>();
		for (Relationship rel : conceptOutline.getRelationships()) {
			Map<String, Relationship> groupMap = newRelGroupMap.computeIfAbsent(rel.getGroupId(), k -> new HashMap<>());
			if (rel.getTarget() != null) {
				groupMap.put(rel.getTarget().getConceptId() + "_"  + rel.getType().getConceptId(), rel);
			} else {
				//handle relationship with slot
				if (rel.getTargetSlot() != null) {
					String slot = rel.getTargetSlot().getSlotName();
					ConceptMiniPojo target = attributeSlotMap.get(slot);
					if (target == null) {
						if ("0".equals(rel.getCardinalityMin()) || rel.getCardinalityMin() == null) {
							//optional
							continue;
						} else {
							throw new ServiceException("Failed to find attribute slot value " + slot);
						}
					}
					String key = target.getConceptId() + "_" + rel.getType().getConceptId();
					groupMap.put(key, rel);
				}
			}
		}
		
		List<List<RelationshipPojo>> mergedSet = constructRelationshipSet(existingRelGroupMap, newRelGroupMap);
		List<RelationshipPojo> relationships = new ArrayList<>();
		RoleGroupNumberGenerator roleGrpNumberGenerator = new RoleGroupNumberGenerator(mergedSet);
		for (List<RelationshipPojo> roleGroup : mergedSet ) {
			int grpNumber = roleGrpNumberGenerator.getRoleGroupNumber(roleGroup);
			for (RelationshipPojo pojo : roleGroup) {
				if (pojo.getGroupId() != grpNumber) {
					pojo.setGroupId(grpNumber);
				}
				if (pojo.getRelationshipId() == null) {
					pojo.setSourceId(conceptToTransform.getConceptId());
				}
				relationships.add(pojo);
			}
		}
		for (RelationshipPojo pojo : statedRels) {
			if (!relationships.contains(pojo)) {
				if (pojo.isActive()) {
					pojo.setActive(false);
					pojo.setEffectiveTime(null);
				}
				// only adding published inactive stated rels
				if (pojo.isReleased() || pojo.isActive()) {
					relationships.add(pojo);
				}
			}
		}
		
		List<RelationshipPojo> inferred = conceptToTransform.getRelationships().stream()
				.filter(r -> r.getCharacteristicType().equals(Constants.INFERRED))
				.collect(Collectors.toList());
		relationships.addAll(inferred);
		
		Set<RelationshipPojo> sortedRels = new TreeSet<RelationshipPojo>(getRelationshipPojoComparator());
		sortedRels.addAll(relationships);
		if (relationships.size() != sortedRels.size()) {
			throw new ServiceException(String.format("The total sorted relationships %s doesn't match the total before sorting %s",relationships.size(), sortedRels.size()));
		}
		conceptToTransform.setRelationships(sortedRels);
	}

	private List<List<RelationshipPojo>> constructRelationshipSet(Map<Integer, Map<String, RelationshipPojo>> existingRelGroupMap,
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
		
		List<List<RelationshipPojo>> mergedSet = new ArrayList<>();
		//match existing role groups
		for (Set<String> relSet : newRelSet) {
			List<RelationshipPojo> pojoSet = new ArrayList<>();
			for (String key : relSet) {
				if (relMapByTargetAndType.containsKey(key)) {
					RelationshipPojo pojo = relMapByTargetAndType.get(key).iterator().next();
					pojoSet.add(pojo);
					if (relMapByTargetAndType.get(key).size() > 1) {
						relMapByTargetAndType.get(key).remove(pojo);
					}
				} else {
					//no need to remove new relationship as it is not published 
					Relationship relationship = newRelMapByTargetAndType.get(key).iterator().next();
					pojoSet.add(constructRelationshipPojo(relationship, attributeSlotMap));
				}
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
		pojo.setModuleId(getModuleId());
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
	
	private String getModuleId() {
		return conceptOutline.getModuleId() !=null ? conceptOutline.getModuleId() : conceptToTransform.getModuleId();
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
	
	public static Comparator<RelationshipPojo> getRelationshipPojoComparator() {
		Comparator<RelationshipPojo> relationshipCompartor = Comparator
				.comparing(RelationshipPojo::getCharacteristicType, Comparator.nullsFirst(String::compareTo).reversed())
				.thenComparing(RelationshipPojo::getSourceId, Comparator.nullsFirst(String::compareTo))
				.thenComparing(RelationshipPojo::getGroupId, Comparator.nullsFirst(Integer::compareTo))
				.thenComparing(RelationshipPojo::isActive, Comparator.nullsFirst(Boolean::compareTo))
				.thenComparing(RelationshipPojo::getRelationshipId, Comparator.nullsFirst(String::compareTo))
				.thenComparing(RelationshipPojo::getType, Comparator.comparing(ConceptMiniPojo::getConceptId, Comparator.nullsFirst(String::compareTo)))
				.thenComparing(RelationshipPojo::getTarget, Comparator.comparing(ConceptMiniPojo::getConceptId, Comparator.nullsFirst(String::compareTo)));
		return relationshipCompartor;
	}
}
