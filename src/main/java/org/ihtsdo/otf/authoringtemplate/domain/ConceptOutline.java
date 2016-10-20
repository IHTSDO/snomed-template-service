package org.ihtsdo.otf.authoringtemplate.domain;

import java.util.ArrayList;
import java.util.List;

public class ConceptOutline {

	private List<Relationship> relationships;
	private List<Description> descriptions;

	public ConceptOutline() {
		relationships = new ArrayList<>();
		descriptions = new ArrayList<>();
	}

	public ConceptOutline addDescription(Description description) {
		descriptions.add(description);
		return this;
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
