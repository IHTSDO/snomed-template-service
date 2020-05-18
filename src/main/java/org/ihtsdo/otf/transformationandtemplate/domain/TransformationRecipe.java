package org.ihtsdo.otf.transformationandtemplate.domain;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.*;

@JsonPropertyOrder({"key", "title", "description", "component", "changeType", "source", "fieldNames", "fieldMap"})
public class TransformationRecipe {

	private String key;
	private String title;
	private String description;
	private ComponentType component;
	private ChangeType changeType;
	private String source;
	private Map<String, Object> fieldMap;

	public Collection<String> getFieldNames() {
		List<String> names = new ArrayList<>();
		for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof String) {
				names.add(key);
			} else if (value instanceof List) {
				@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) value;
				int count = 0;
				for (Object listValue : list) {
					count++;
					if (listValue instanceof Map) {
						@SuppressWarnings("unchecked")
						Map<String, String> map = (Map<String, String>) listValue;
						names.addAll(map.keySet());
					} else if (listValue instanceof String) {
						names.add(key + count);
					}
				}
			}
		}
		return names;
	}

	public String getKey() {
		return key;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public ComponentType getComponent() {
		return component;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public String getSource() {
		return source;
	}

	public Map<String, Object> getFieldMap() {
		return fieldMap;
	}

}
