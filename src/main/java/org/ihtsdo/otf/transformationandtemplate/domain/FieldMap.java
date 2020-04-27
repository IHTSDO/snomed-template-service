package org.ihtsdo.otf.transformationandtemplate.domain;

import java.util.Map;
import java.util.Set;

public class FieldMap {

	// Concept
	private String conceptId;

	// Description
	private String descriptionId;
	private String term;
	private String lang;
	private String caseSignificance;
	private String type;
	private Map<String, String> acceptability;

	// Inactivation
	private String inactivationIndicator;
	private Set<String> associationTargets;

	public String getConceptId() {
		return conceptId;
	}

	public String getDescriptionId() {
		return descriptionId;
	}

	public String getTerm() {
		return term;
	}

	public String getLang() {
		return lang;
	}

	public String getCaseSignificance() {
		return caseSignificance;
	}

	public String getType() {
		return type;
	}

	public Map<String, String> getAcceptability() {
		return acceptability;
	}

	public String getInactivationIndicator() {
		return inactivationIndicator;
	}

	public Set<String> getAssociationTargets() {
		return associationTargets;
	}
}
