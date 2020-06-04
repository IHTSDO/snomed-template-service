package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import org.ihtsdo.otf.transformationandtemplate.service.componenttransform.valueprovider.ValueProvider;
import org.ihtsdo.otf.transformationandtemplate.service.componenttransform.valueprovider.ValueProviderFactory;

import java.util.*;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class TSVComponentTransformation implements ComponentTransformation {

	private final Map<String, Object> fieldMap;
	private final String[] columns;
	private final ValueProviderFactory valueProviderFactory = new ValueProviderFactory();

	public TSVComponentTransformation(Map<String, Object> fieldMap, String[] columns) {
		this.fieldMap = fieldMap;
		this.columns = columns;
	}

	@Override
	public String getValueString(String fieldName) {
		Object mapping = fieldMap.get(fieldName);
		if (mapping != null) {
			if (mapping instanceof String) {
				ValueProvider valueProvider = valueProviderFactory.getProvider((String) mapping);
				return valueProvider.getValue(columns);
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
					ValueProvider valueProvider = valueProviderFactory.getProvider(itemMapping);
					String value = valueProvider.getValue(columns);
					if (!isEmpty(value)) {
						valueList.add(value);
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
					String key = valueProviderFactory.getProvider(values.next()).getValue(columns);
					String value = valueProviderFactory.getProvider(values.next()).getValue(columns);
					if (!isEmpty(key) && !isEmpty(value)) {
						valueMap.put(key, value);
					}
				}
				return valueMap;
			}
		}
		return null;
	}

}
