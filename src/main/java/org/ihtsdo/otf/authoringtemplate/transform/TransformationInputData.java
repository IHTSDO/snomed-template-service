package org.ihtsdo.otf.authoringtemplate.transform;

import java.util.Map;
import java.util.Set;

import org.ihtsdo.otf.rest.client.snowowl.pojo.SimpleConceptPojo;
import org.snomed.authoringtemplate.domain.ConceptTemplate;

public class TransformationInputData {

	private String branchPath;
	private ConceptTemplate destinationTemplate;
	private Map<String, SimpleConceptPojo> conceptIdMap;
	private String inactivationReason;
	private Map<String, Set<String>> destinationAttributeTypeSlotMap;


	public void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}

	public String getBranchPath() {
		return this.branchPath;
	}

	public void setDestinationTemplate(ConceptTemplate destination) {
		this.destinationTemplate = destination;
	}

	public ConceptTemplate getDestinationTemplate() {
		return destinationTemplate;
	}

	public void setDestination(ConceptTemplate destination) {
		this.destinationTemplate = destination;
	}

	public String getInactivationReason() {
		return this.inactivationReason;
	}

	public void setConceptIdMap(Map<String, SimpleConceptPojo> conceptIdMap) {
		this.conceptIdMap = conceptIdMap;
	}

	public Map<String, SimpleConceptPojo> getConceptIdMap() {
		return conceptIdMap;
	}

	public void setInactivationReason(String inactivationReason) {
		this.inactivationReason = inactivationReason;
	}

	public void setDestinationAttributeTypeSlotMap(Map<String, Set<String>> attributeTypeSlotMap) {
		destinationAttributeTypeSlotMap = attributeTypeSlotMap;
	}

	public Map<String, Set<String>> getDestinationAttributeTypeSlotMap() {
		return destinationAttributeTypeSlotMap;
	}
	
}
