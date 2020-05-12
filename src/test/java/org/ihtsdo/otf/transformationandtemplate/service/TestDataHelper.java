package org.ihtsdo.otf.transformationandtemplate.service;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.AxiomPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RelationshipPojo;
import org.snomed.authoringtemplate.domain.*;

import java.util.*;

import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DefinitionStatus.FULLY_DEFINED;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.CaseSignificance.CASE_INSENSITIVE;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Type.*;

public class TestDataHelper {

	public static final String INFERRED_RELATIONSHIP = "INFERRED_RELATIONSHIP";
	public static final String STATED_RELATIONSHIP = "STATED_RELATIONSHIP";


	public static ConceptPojo createConceptPojo() {
		ConceptPojo pojo = new ConceptPojo();
		pojo.setActive(true);
		pojo.setModuleId("900000000000012004");
		pojo.setConceptId("123456");
		pojo.setDefinitionStatus(FULLY_DEFINED);
		Set<DescriptionPojo> descriptions = createDescriptionPojos();
		pojo.setDescriptions(descriptions);
		Set<RelationshipPojo> relationships = createRelationshipPojos("123456", true);
		
		Set<AxiomPojo> classAxioms = new HashSet<>();
		AxiomPojo classAxiom = new  AxiomPojo();
		classAxiom.setAxiomId(UUID.randomUUID().toString());
		classAxiom.setDefinitionStatusId(FULLY_DEFINED.getConceptId());
		classAxiom.setRelationships(relationships);
		classAxioms.add(classAxiom);
		pojo.setClassAxioms(classAxioms);
		return pojo;
	}
	

	public static RelationshipPojo createRelationshipPojo(int groupNum, String relationshipId) {
		return createRelationshipPojo(groupNum, "246075003", relationshipId);
	}
	
	public static RelationshipPojo createRelationshipPojo(int groupNum, String type, String relationshipId) {
		RelationshipPojo rel = new RelationshipPojo(groupNum, type, "6543217", STATED_RELATIONSHIP);
		rel.setActive(true);
		if (relationshipId != null) {
			rel.setRelationshipId(relationshipId);
			rel.setReleased(true);
		}
		return rel;
	}

	public static Set<RelationshipPojo> createRelationshipPojos(String sourceId, boolean isStated) {
		String characteristicType = isStated ? STATED_RELATIONSHIP : INFERRED_RELATIONSHIP;
		Set<RelationshipPojo> pojos = new HashSet<>();
		RelationshipPojo rel1 = new RelationshipPojo(0, "116680003", "654321", characteristicType);
		RelationshipPojo rel2 = new RelationshipPojo(0, "246075003", "6543217", characteristicType);
		rel1.setSourceId(sourceId);
		rel2.setSourceId(sourceId);
		pojos.add(rel1);
		pojos.add(rel2);
		RelationshipPojo rel3 = new RelationshipPojo(1, "370135005", "65432176", characteristicType);
		RelationshipPojo rel4 = new RelationshipPojo(1, "246075003", "65432178", characteristicType);
		rel3.setSourceId(sourceId);
		rel4.setSourceId(sourceId);
		pojos.add(rel3);
		pojos.add(rel4);
		return pojos;
	}

	public static Set<DescriptionPojo> createDescriptionPojos() {
		Set<DescriptionPojo> pojos = new HashSet<>();
		DescriptionPojo pojo = new DescriptionPojo();
		pojo.setReleased(true);
		pojo.setActive(true);
		pojo.setCaseSignificance(CASE_INSENSITIVE);
		pojo.setTerm("Allergy to almond");
		pojo.setType(SYNONYM);
		pojo.setAcceptabilityMap(constructAcceptabilityMap(DescriptionPojo.Acceptability.PREFERRED, DescriptionPojo.Acceptability.PREFERRED));
		pojos.add(pojo);
		
		DescriptionPojo fsn = new DescriptionPojo();
		fsn.setReleased(true);
		fsn.setActive(true);
		fsn.setCaseSignificance(CASE_INSENSITIVE);
		fsn.setTerm("Allergy to almond (disorder)");
		fsn.setType(FSN);
		fsn.setAcceptabilityMap(constructAcceptabilityMap(DescriptionPojo.Acceptability.PREFERRED, DescriptionPojo.Acceptability.PREFERRED));
		pojos.add(fsn);
		
		DescriptionPojo textDefinition = new DescriptionPojo();
		textDefinition.setReleased(true);
		textDefinition.setActive(true);
		textDefinition.setCaseSignificance(CASE_INSENSITIVE);
		textDefinition.setTerm("Allergy to almond text definition");
		textDefinition.setType(TEXT_DEFINITION);
		textDefinition.setAcceptabilityMap(constructAcceptabilityMap(DescriptionPojo.Acceptability.PREFERRED, DescriptionPojo.Acceptability.PREFERRED));
		pojos.add(textDefinition);
		return pojos;
	}


	public static Map<String, String> constructAcceptabilityMapStrings(String usValue, String gbValue) {
		Map<String, String> result = new HashMap<>();
		result.put("900000000000509007", usValue);
		result.put("900000000000508004", gbValue);
		return result;
	}

	public static Map<String, DescriptionPojo.Acceptability> constructAcceptabilityMap(DescriptionPojo.Acceptability usValue, DescriptionPojo.Acceptability gbValue) {
		Map<String, DescriptionPojo.Acceptability> result = new HashMap<>();
		result.put("900000000000509007", usValue);
		result.put("900000000000508004", gbValue);
		return result;
	}


	public static ConceptOutline createConceptOutline() {
		ConceptOutline conceptOutline = new ConceptOutline();
		List<Description> descriptions = new ArrayList<>();
		Description fsn = new Description();
		fsn.setTermTemplate("Allergy to $substance$ (finding)");
		fsn.setCaseSignificance(CaseSignificance.CASE_INSENSITIVE);
		fsn.setType(DescriptionType.FSN);
		descriptions.add(fsn);
		
		Description preferedTerm = new Description();
		preferedTerm.setTermTemplate("Allergy to $substance$");
		preferedTerm.setCaseSignificance(CaseSignificance.CASE_INSENSITIVE);
		preferedTerm.setType(DescriptionType.SYNONYM);
		descriptions.add(preferedTerm);
		
		conceptOutline.setDescriptions(descriptions);
		List<Relationship> relationships = new ArrayList<>();
		Relationship rel1 = new Relationship();
		rel1.setCardinalityMin("1");
		rel1.setCardinalityMax("1");
		rel1.setGroupId(0);
		rel1.setType(new ConceptMini("116680003"));
		rel1.setTarget(new ConceptMini("420134006"));
		
		Relationship rel2 = new Relationship();
		rel2.setCardinalityMin("1");
		rel2.setCardinalityMax("1");
		rel2.setGroupId(0);
		rel2.setType(new ConceptMini("116680003"));
		rel2.setTarget(new ConceptMini("473011001"));
		relationships.add(rel1);
		relationships.add(rel2);
		
		Relationship rel3 = new Relationship();
		rel3.setCardinalityMin("1");
		rel3.setCardinalityMax("1");
		rel3.setGroupId(1);
		rel3.setType(new ConceptMini("719722006"));
		rel3.setTarget(new ConceptMini("472964009"));
		
		Relationship rel4 = new Relationship();
		rel4.setCardinalityMin("1");
		rel4.setCardinalityMax("1");
		rel4.setGroupId(1);
		rel4.setType(new ConceptMini("246075003"));
		rel4.setTargetSlot(new SimpleSlot("substance", "<105590001 |Substance (substance)|"));
		relationships.add(rel3);
		relationships.add(rel4);
		
		Axiom axiom = new Axiom();
		axiom.setRelationships(relationships);
		return conceptOutline.addAxiom(axiom);
	}


	public static ConceptPojo createCTGuidedProcedureConcept(boolean withOptionalGroup) {
		ConceptPojo pojo = new ConceptPojo();
		pojo.setActive(true);
		pojo.setModuleId("900000000000012004");
		pojo.setConceptId("1234445");
		pojo.setDefinitionStatus(org.ihtsdo.otf.rest.client.terminologyserver.pojo.DefinitionStatus.FULLY_DEFINED);
		Set<DescriptionPojo> descriptions = createDescriptionPojos();
		pojo.setDescriptions(descriptions);
		List<Map<String, String>> typeAndValues = new ArrayList<>();
		Map<String, String> group0 = new HashMap<>();
		group0.put("116680003", "71388002");
		
		Map<String, String> group1 = new HashMap<>();
		group1.put("260686004", "312251004");
		group1.put("405813007", "442083009");
		group1.put("363703001", "429892002");
		typeAndValues.add(group0);
		typeAndValues.add(group1);
		
		if (withOptionalGroup) {
			Map<String, String> group2 = new HashMap<>();
			group2.put("260686004", "129264002");
			group2.put("405813007", "442083009");
			typeAndValues.add(group2);
		}
		AxiomPojo axiomPojo = new AxiomPojo();
		axiomPojo.setActive(true);
		axiomPojo.setAxiomId(UUID.randomUUID().toString());
		Set<RelationshipPojo> relationships = createRelationshipPojos("1234445", true, typeAndValues);
		axiomPojo.setRelationships(relationships);
		Set<AxiomPojo> classAxioms = new HashSet<>();
		classAxioms.add(axiomPojo);
		pojo.setClassAxioms(classAxioms);
		return pojo;
	}
	
	public static Set<RelationshipPojo> createRelationshipPojos(String sourceId, boolean isStated, List<Map<String,String>> typeAndValues) {
		String characteristicType = isStated ? STATED_RELATIONSHIP : INFERRED_RELATIONSHIP;
		Set<RelationshipPojo> pojos = new HashSet<>();
		int group = 0;
		for (Map<String, String> typeAndValueMap : typeAndValues) {
			for (String type : typeAndValueMap.keySet()) {
				RelationshipPojo relationship = new RelationshipPojo(group, type, typeAndValueMap.get(type), characteristicType);
				relationship.setSourceId(sourceId);
				pojos.add(relationship);
			}
			group++;
		}
		return pojos;
	}


	public static Map<String, Set<DescriptionPojo>> constructSlotDescriptionValuesMap(Map<String, String> slotValueMap,
			Map<String, CaseSignificance> slotCSMap, DescriptionType type) {

		Map<String, Set<DescriptionPojo>> results = new HashMap<>();
		for (String slot : slotValueMap.keySet()) {
			results.put(slot, new HashSet<>());
			DescriptionPojo pojo = new DescriptionPojo();
			pojo.setReleased(true);
			pojo.setActive(true);
			if (slotCSMap == null || slotCSMap.isEmpty()) {
				pojo.setCaseSignificance(CASE_INSENSITIVE);
			} else {
				pojo.setCaseSignificance(DescriptionPojo.CaseSignificance.valueOf(slotCSMap.get(slot).name()));
			}
			pojo.setTerm(slotValueMap.get(slot));
			pojo.setType(DescriptionPojo.Type.valueOf(type.name()));
			pojo.setAcceptabilityMap(constructAcceptabilityMap(DescriptionPojo.Acceptability.PREFERRED, DescriptionPojo.Acceptability.PREFERRED));
			results.get(slot).add(pojo);
		}
		return results;
	}

}
