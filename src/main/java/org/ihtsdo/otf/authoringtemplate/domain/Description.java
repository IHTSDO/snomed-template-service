package org.ihtsdo.otf.authoringtemplate.domain;

import java.util.HashMap;
import java.util.Map;

public class Description {

	private DescriptionType type;
	private String lang;
	private String term;
	private String termTemplate;
	private String initialTerm;
	private CaseSignificance caseSignificance;
	private Map<String, String> acceptabilityMap;

	public Description() {
		acceptabilityMap = new HashMap<>();
	}

	public Description(String termTemplate) {
		this.termTemplate = termTemplate;
	}

	public DescriptionType getType() {
		return type;
	}

	public void setType(DescriptionType type) {
		this.type = type;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getTermTemplate() {
		return termTemplate;
	}

	public void setTermTemplate(String termTemplate) {
		this.termTemplate = termTemplate;
	}

	public String getInitialTerm() {
		return initialTerm;
	}

	public void setInitialTerm(String initialTerm) {
		this.initialTerm = initialTerm;
	}

	public CaseSignificance getCaseSignificance() {
		return caseSignificance;
	}

	public void setCaseSignificance(CaseSignificance caseSignificance) {
		this.caseSignificance = caseSignificance;
	}

	public Map<String, String> getAcceptabilityMap() {
		return acceptabilityMap;
	}

	public void setAcceptabilityMap(Map<String, String> acceptabilityMap) {
		this.acceptabilityMap = acceptabilityMap;
	}
}
