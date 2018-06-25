package org.ihtsdo.otf.authoringtemplate.transform;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.ihtsdo.otf.authoringtemplate.domain.ConceptTemplate;
import org.ihtsdo.otf.authoringtemplate.domain.logical.LogicalTemplate;

public class TransformationInputData {

	private Set<String> synonymTemplates;
	private Set<String> fsnTemplates;
	private Map<Pattern, List<String>> synonymPatterns;
	private Map<Pattern, List<String>> fsnPatterns;
	private LogicalTemplate sourceLogicalTemplate;
	private String branchPath;
	private ConceptTemplate destinationTemplate;
	private Map<String, String> conceptFsnMap;
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

	public void setSynonymPatterns(Map<Pattern, List<String>> synonymPatterns) {
		this.synonymPatterns = synonymPatterns;
	}

	public void setFsnPatterns(Map<Pattern, List<String>> fsnPatterns) {
		this.fsnPatterns = fsnPatterns;
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

	public Map<Pattern, List<String>> getSynonymPatterns() {
		return synonymPatterns;
	}

	public Map<Pattern, List<String>> getFsnPatterns() {
		return fsnPatterns;
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

	public void setConceptFsnMap(Map<String, String> conceptFsnMap) {
		this.conceptFsnMap = conceptFsnMap;
	}

	public Map<String, String> getConceptFsnMap() {
		return conceptFsnMap;
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
