package org.ihtsdo.otf.transformationandtemplate.domain;

import java.util.*;

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

	public Collection<String> getFieldNames() {
		List<String> names = new ArrayList<>();
		for (Object value : fieldMap.values()) {
			if (value instanceof String) {
				names.add((String) value);
			} else if (value instanceof List) {
				@SuppressWarnings("unchecked")
				List<Map<String, String>> list = (List<Map<String, String>>) value;
				for (Map<String, String> map : list) {
					names.addAll(map.values());
				}
			}
		}
		return names;
	}
}
