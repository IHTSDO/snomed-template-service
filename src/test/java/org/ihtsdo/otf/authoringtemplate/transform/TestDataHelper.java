package org.ihtsdo.otf.authoringtemplate.transform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.RelationshipPojo;
import org.snomed.authoringtemplate.domain.CaseSignificance;
import org.snomed.authoringtemplate.domain.ConceptMini;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.Description;
import org.snomed.authoringtemplate.domain.DescriptionType;
import org.snomed.authoringtemplate.domain.Relationship;
import org.snomed.authoringtemplate.domain.SimpleSlot;

public class TestDataHelper {

	public static final String INFERRED_RELATIONSHIP = "INFERRED_RELATIONSHIP";
	public static final String STATED_RELATIONSHIP = "STATED_RELATIONSHIP";


	public static ConceptPojo createConceptPojo() {
		ConceptPojo pojo = new ConceptPojo();
		pojo.setActive(true);
		pojo.setModuleId("900000000000012004");
		pojo.setConceptId("123456");
		pojo.setDefinitionStatus(org.ihtsdo.otf.rest.client.snowowl.pojo.DefinitionStatus.FULLY_DEFINED);
		Set<DescriptionPojo> descriptions = createDescriptionPojos();
		pojo.setDescriptions(descriptions);
		Set<RelationshipPojo> relationships = createRelationshipPojos("123456", true);
		pojo.setRelationships(relationships);
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
		pojo.setCaseSignificance("ci");
		pojo.setTerm("Allergy to almond");
		pojo.setType(DescriptionType.SYNONYM.name());
		pojos.add(pojo);
		
		DescriptionPojo fsn = new DescriptionPojo();
		fsn.setReleased(true);
		fsn.setActive(true);
		fsn.setCaseSignificance("ci");
		fsn.setTerm("Allergy to almond (disorder)");
		fsn.setType(DescriptionType.FSN.name());
		pojos.add(fsn);
		return pojos;
	}


	public static ConceptOutline createConceptOutline() {
		ConceptOutline conceptOutline = new ConceptOutline();
		List<Description> descriptions = new ArrayList<>();
		Description fsn = new Description();
		fsn.setTerm("Allergy to almond (finding)");
		fsn.setCaseSignificance(CaseSignificance.CASE_INSENSITIVE);
		fsn.setType(DescriptionType.FSN);
		descriptions.add(fsn);
		Description preferedTerm = new Description();
		preferedTerm.setTerm("Allergy to almond");
		preferedTerm.setType(DescriptionType.SYNONYM);
		preferedTerm.setCaseSignificance(CaseSignificance.CASE_INSENSITIVE);
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
		conceptOutline.setRelationships(relationships);
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
		return conceptOutline;
	}
}
