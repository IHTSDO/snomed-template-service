package org.ihtsdo.otf.authoringtemplate.transform;

import java.util.Map;
import java.util.Set;

import org.ihtsdo.otf.rest.client.snowowl.pojo.SimpleConceptPojo;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.logical.LogicalTemplate;

public class TransformationInputData {

	private Set<String> synonymTemplates;
	private Set<String> fsnTemplates;
	private LogicalTemplate sourceLogicalTemplate;
	private String branchPath;
	private ConceptTemplate destinationTemplate;
	private Map<String, SimpleConceptPojo> conceptIdMap;
	private String inactivationReason;
	private Map<String, Set<String>> sourceAttributeTypeSlotMap;

	public void setSynonymTemplates(Set<String> synonymTemplates) {
		this.synonymTemplates = synonymTemplates;
	}

	public void setFsnTemplates(Set<String> fsnTemplates) {
		this.fsnTemplates = fsnTemplates;
	}
	
	public Set<String> getFsnTemplates() {
		return fsnTemplates;
	}

	public void setSourceLogicalTemplate(LogicalTemplate logical) {
		this.sourceLogicalTemplate = logical;
	}

	public void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}

	public String getBranchPath() {
		return this.branchPath;
	}

	public Set<String> getSynonymTemplates() {
		return synonymTemplates;
	}

	public LogicalTemplate getSourceLogicalTemplate() {
		return sourceLogicalTemplate;
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

	public void setSourceAttributeTypeSlotMap(Map<String, Set<String>> attributeTypeSlotMap) {
		sourceAttributeTypeSlotMap = attributeTypeSlotMap;
	}

	public Map<String, Set<String>> getSourceAttributeTypeSlotMap() {
		return sourceAttributeTypeSlotMap;
	}
	
}
