package org.ihtsdo.otf.authoringtemplate.domain;

public class ConceptMini {

	private String conceptId;

	public ConceptMini() {
	}

	public ConceptMini(String conceptId) {
		this.conceptId = conceptId;
	}

	public String getConceptId() {
		return conceptId;
	}

	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

	@Override
	public String toString() {
		return "ConceptMini{" +
				"conceptId='" + conceptId + '\'' +
				'}';
	}
}
