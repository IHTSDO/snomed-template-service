package org.ihtsdo.otf.transformationandtemplate.transform;

public class ResourcePathHelper {

	private static final String RESULTS_JSON = "results.json";

	private static final String STATUS_JSON = "status.json";

	public static String getResultPath(String transformationId) {
		return getFilePath(transformationId, RESULTS_JSON);
	}
	
	public static String getStatusPath(String transformationId) {
		return getFilePath(transformationId, STATUS_JSON);
	}
	
	private static String getFilePath(String transformationId, String relativePath) {
		return transformationId + "/" + relativePath;
	}
}