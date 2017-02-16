package org.ihtsdo.otf.authoringtemplate.domain;

import java.util.ArrayList;
import java.util.List;

public class ConceptOutline {

	private DefinitionStatus definitionStatus;
	private List<Relationship> relationships;
	private List<Description> descriptions;

	public ConceptOutline() {
		relationships = new ArrayList<>();
		descriptions = new ArrayList<>();
	}

	public ConceptOutline(DefinitionStatus definitionStatus) {
		this();
		this.definitionStatus = definitionStatus;
	}

	public ConceptOutline addDescription(Description description) {
		descriptions.add(description);
		return this;
	}

	public void addRelationship(Relationship relationship) {
		relationships.add(relationship);
	}

	public DefinitionStatus getDefinitionStatus() {
		return definitionStatus;
	}

	public void setDefinitionStatus(DefinitionStatus definitionStatus) {
		this.definitionStatus = definitionStatus;
	}

	public List<Relationship> getRelationships() {
		return relationships;
	}

	public void setRelationships(List<Relationship> relationships) {
		this.relationships = relationships;
	}

	public List<Description> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<Description> descriptions) {
		this.descriptions = descriptions;
	}
}
