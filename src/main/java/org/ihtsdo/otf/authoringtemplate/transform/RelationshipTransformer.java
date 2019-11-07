package org.ihtsdo.otf.authoringtemplate.transform;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ihtsdo.otf.authoringtemplate.service.Constants;
import org.ihtsdo.otf.authoringtemplate.service.TemplateUtil;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.AxiomPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptMiniPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RelationshipPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.Relationship;

public class RelationshipTransformer {

	private ConceptPojo conceptToTransform;
	private ConceptOutline conceptOutline;
	private Map<String, ConceptMiniPojo> attributeSlotMap;
	private Map<String, ConceptMiniPojo> conceptIdMap;
	private Logger logger = LoggerFactory.getLogger(getClass());

	public RelationshipTransformer(ConceptPojo conceptToTransform, ConceptOutline conceptOutline,
			Map<String, ConceptMiniPojo> attributeSlotMap, Map<String, ConceptMiniPojo> conceptIdMap) {
		this.conceptToTransform = conceptToTransform;
		this.conceptOutline = conceptOutline;
		this.attributeSlotMap = attributeSlotMap;
		this.conceptIdMap = conceptIdMap;
	}

	public void transform() throws ServiceException {
		if (conceptToTransform.getClassAxioms() == null || conceptToTransform.getClassAxioms().isEmpty()) {
			throw new ServiceException("No class axioms available to transform for concept " + conceptToTransform.getConceptId() );
		}
		
		if (conceptToTransform.getClassAxioms().size() > 1) {
			throw new UnsupportedOperationException("Transformation for concepts with multiple class axioms is not implemented yet. Concept id = " + conceptToTransform.getConceptId());
		}
		
		AxiomPojo classAxiom = conceptToTransform.getClassAxioms().iterator().next();
		//map relationship by group
		Map<Integer, Map<String, RelationshipPojo>> existingRelGroupMap = new HashMap<>();
		for (RelationshipPojo pojo : classAxiom.getRelationships()) {
			if (pojo.isActive()) {
				existingRelGroupMap.computeIfAbsent(pojo.getGroupId(), k -> new HashMap<>())
				.put(pojo.getTarget().getConceptId() + "_" +  pojo.getType().getConceptId(), pojo);
			}
		}

		Map<Integer, Map<String, Relationship>> newRelGroupMap = new HashMap<>();
		List<Relationship> relationships = conceptOutline.getClassAxioms().stream().findFirst().get().getRelationships();
		
		for (Relationship rel : relationships) {
			Map<String, Relationship> groupMap = newRelGroupMap.computeIfAbsent(rel.getGroupId(), k -> new HashMap<>());
			if (rel.getTarget() != null) {
				groupMap.put(rel.getTarget().getConceptId() + "_"  + rel.getType().getConceptId(), rel);
			} else {
				//handle relationship with slot
				if (rel.getTargetSlot() != null) {
					String slot = rel.getTargetSlot().getSlotName();
					ConceptMiniPojo target = attributeSlotMap.get(slot);
					if (target == null) {
						if (TemplateUtil.isOptional(rel)) {
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
		Set<RelationshipPojo> transformedRels = new HashSet<>();
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
				transformedRels.add(pojo);
			}
		}
		Set<RelationshipPojo> sortedRels = new TreeSet<RelationshipPojo>(getRelationshipPojoComparator());
		sortedRels.addAll(transformedRels);
		if (transformedRels.size() != sortedRels.size()) {
			throw new ServiceException(String.format("The total sorted relationships %s doesn't match the total before sorting %s",transformedRels.size(), sortedRels.size()));
		}
		//Only for one axiom at the moment.
		classAxiom.setEffectiveTime(null);
		classAxiom.setRelationships(transformedRels);
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
		if (!conceptIdMap.containsKey(conceptId)) {
			logger.error("Prefetched concepts map doesn't contain concept id " + conceptId);
			return new ConceptMiniPojo(conceptId);		
		} else {
			return conceptIdMap.get(conceptId);
		}
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
