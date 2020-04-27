package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import java.util.Map;

public interface ComponentTransformation {
	String getValueString(String fieldName);

	Map<String, String> getValueMap(String fieldName);
}
