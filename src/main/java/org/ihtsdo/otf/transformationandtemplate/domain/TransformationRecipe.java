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
		for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof String) {
				names.add(key);
			} else if (value instanceof List) {
				@SuppressWarnings("unchecked")
				List<Map<String, String>> list = (List<Map<String, String>>) value;
				for (Map<String, String> map : list) {
					names.addAll(map.keySet());
				}
			}
		}
		return names;
	}
}
