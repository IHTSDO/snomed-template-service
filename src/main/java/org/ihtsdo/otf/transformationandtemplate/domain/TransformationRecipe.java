package org.ihtsdo.otf.transformationandtemplate.domain;

import java.util.Map;

public class TransformationRecipe {

	private String name;
	private String source;
	private ComponentType component;
	private ChangeType changeType;
	private Map<String, Object> fieldMap;

	public String getName() {
		return name;
	}

	public String getSource() {
		return source;
	}

	public ComponentType getComponent() {
		return component;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public Map<String, Object> getFieldMap() {
		return fieldMap;
	}
}
