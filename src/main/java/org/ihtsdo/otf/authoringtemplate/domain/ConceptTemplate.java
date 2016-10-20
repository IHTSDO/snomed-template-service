package org.ihtsdo.otf.authoringtemplate.domain;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"name", "focusConcept", "version", "logicalTemplate", "lexicalTemplate", "conceptOutline"})
public class ConceptTemplate {

	private String name;
	private String focusConcept;
	private int version;

	private String logicalTemplate;
	private List<LexicalTemplate> lexicalTemplates;

	private ConceptOutline conceptOutline;

	public ConceptTemplate() {
		lexicalTemplates = new ArrayList<>();
	}

	public void bumpVersion() {
		version++;
	}

	public void addLexicalTemplate(LexicalTemplate lexicalTemplate) {
		lexicalTemplates.add(lexicalTemplate);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFocusConcept() {
		return focusConcept;
	}

	public void setFocusConcept(String focusConcept) {
		this.focusConcept = focusConcept;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getLogicalTemplate() {
		return logicalTemplate;
	}

	public void setLogicalTemplate(String logicalTemplate) {
		this.logicalTemplate = logicalTemplate;
	}

	public List<LexicalTemplate> getLexicalTemplates() {
		return lexicalTemplates;
	}

	public void setLexicalTemplates(List<LexicalTemplate> lexicalTemplates) {
		this.lexicalTemplates = lexicalTemplates;
	}

	public ConceptOutline getConceptOutline() {
		return conceptOutline;
	}

	public void setConceptOutline(ConceptOutline conceptOutline) {
		this.conceptOutline = conceptOutline;
	}
}
