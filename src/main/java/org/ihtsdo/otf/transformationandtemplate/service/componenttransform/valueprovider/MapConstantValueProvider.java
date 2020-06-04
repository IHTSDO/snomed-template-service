package org.ihtsdo.otf.transformationandtemplate.service.componenttransform.valueprovider;

import java.util.Map;

public class MapConstantValueProvider implements ValueProvider {

	private final ValueProvider providerToWrap;
	private final Map<String, String> constantMap;

	public MapConstantValueProvider(ValueProvider providerToWrap, Map<String, String> constantMap) {
		this.providerToWrap = providerToWrap;
		this.constantMap = constantMap;
	}

	@Override
	public String getValue(String[] columns) {
		// Attempt to map the value, otherwise return the original.
		// This allows constants or SCTIDs to be used in input
		String value = providerToWrap.getValue(columns);
		if (value != null) {
			String mappedValue = constantMap.get(value);
			if (mappedValue != null) {
				return mappedValue;
			}
			// Try case insensitive match, but only after case sensitive try above.
			for (String key : constantMap.keySet()) {
				if (key.equalsIgnoreCase(value)) {
					return constantMap.get(key);
				}
			}
		}
		return value;
	}
}
