package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TSVComponentTransformation implements ComponentTransformation {

	private static final Pattern TSV_FIELD_INDEX_PATTERN = Pattern.compile("\\$tsv.([0-9]{1,2})");

	private final Map<String, Object> fieldMap;
	private final String[] columns;

	public TSVComponentTransformation(Map<String, Object> fieldMap, String[] columns) {
		this.fieldMap = fieldMap;
		this.columns = columns;
	}

	@Override
	public String getValueString(String fieldName) {
		Object mapping = fieldMap.get(fieldName);
		if (mapping != null) {
			if (mapping instanceof String) {
				int tsvIndex = getTsvIndex((String) mapping);
				if (tsvIndex > -1 && columns.length > tsvIndex) {
					return columns[tsvIndex];
				}
			}
		}
		return null;
	}

	@Override
	public List<String> getValueList(String fieldName) {
		Object mapping = fieldMap.get(fieldName);
		if (mapping != null) {
			if (mapping instanceof List) {
				List<String> valueList = new ArrayList<>();
				@SuppressWarnings("unchecked")
				List<String> mappingList = (List<String>) mapping;
				for (String itemMapping : mappingList) {
					int tsvIndex = getTsvIndex(itemMapping);
					if (tsvIndex > -1 && columns.length > tsvIndex) {
						valueList.add(columns[tsvIndex]);
					}
				}
				return valueList;
			}
		}
		return null;
	}

	@Override
	public Map<String, String> getValueMap(String fieldName) {
		Object mapping = fieldMap.get(fieldName);
		if (mapping != null) {
			if (mapping instanceof List) {
				@SuppressWarnings("unchecked")
				List<Map<String, String>> listOfMappings = (List<Map<String, String>>) mapping;
				Map<String, String> valueMap = new HashMap<>();
				for (Map<String, String> keyPairMapping : listOfMappings) {
					Iterator<String> values = keyPairMapping.values().iterator();
					int keyIndex = getTsvIndex(values.next());
					int valueIndex = getTsvIndex(values.next());
					if (keyIndex > -1 && columns.length > keyIndex && valueIndex > -1 && columns.length > valueIndex) {
						valueMap.put(columns[keyIndex], columns[valueIndex]);
					}
				}
				return valueMap;
			}
		}
		return null;
	}

	private int getTsvIndex(String tsvFieldRef) {
		Matcher matcher = TSV_FIELD_INDEX_PATTERN.matcher(tsvFieldRef);
		if (matcher.matches()) {
			return Integer.parseInt(matcher.group(1));
		}
		return -1;
	}

}
