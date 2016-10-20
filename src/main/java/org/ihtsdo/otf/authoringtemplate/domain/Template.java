package org.ihtsdo.otf.authoringtemplate.domain;

import java.util.ArrayList;
import java.util.List;

public class Template {

	private List<String> focusConcepts;
	private List<AttributeGroup> attributeGroups;

	public Template() {
		focusConcepts = new ArrayList<>();
		attributeGroups = new ArrayList<>();
	}

	public void addFocusConcept(String sctid) {
		focusConcepts.add(sctid);
	}

	public void addAttributeGroup(AttributeGroup attributeGroup) {
		attributeGroups.add(attributeGroup);
	}

	public List<String> getFocusConcepts() {
		return focusConcepts;
	}

	public List<AttributeGroup> getAttributeGroups() {
		return attributeGroups;
	}
}
