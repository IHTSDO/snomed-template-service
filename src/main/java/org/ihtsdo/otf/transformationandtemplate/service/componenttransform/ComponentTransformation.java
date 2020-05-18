package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import java.util.List;
import java.util.Map;

public interface ComponentTransformation {
	String getValueString(String fieldName);

	List<String> getValueList(String fieldName);

	Map<String, String> getValueMap(String fieldName);
}
