package org.ihtsdo.otf.authoringtemplate.domain.logical;

import java.util.ArrayList;
import java.util.List;

public class LogicalTemplate {

	private List<String> focusConcepts;
	private List<Attribute> ungroupedAttributes;
	private List<AttributeGroup> attributeGroups;

	public LogicalTemplate() {
		focusConcepts = new ArrayList<>();
		ungroupedAttributes = new ArrayList<>();
		attributeGroups = new ArrayList<>();
	}

	public void addFocusConcept(String sctid) {
		focusConcepts.add(sctid);
	}

	public void addUngroupedAttribute(Attribute attribute) {
		ungroupedAttributes.add(attribute);
	}

	public void addAttributeGroup(AttributeGroup attributeGroup) {
		attributeGroups.add(attributeGroup);
	}

	public List<String> getFocusConcepts() {
		return focusConcepts;
	}

	public List<Attribute> getUngroupedAttributes() {
		return ungroupedAttributes;
	}

	public List<AttributeGroup> getAttributeGroups() {
		return attributeGroups;
	}
}
