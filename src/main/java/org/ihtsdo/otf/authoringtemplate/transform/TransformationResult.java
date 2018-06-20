package org.ihtsdo.otf.authoringtemplate.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;

public class TransformationResult {
	
	private List<ConceptPojo> transformedConcepts;
	private Map<String, String> errors;
	
	public TransformationResult() {
		transformedConcepts = new ArrayList<>();
	}
	
	public void addTransformedConcept(ConceptPojo transformed) {
		transformedConcepts.add(transformed);
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}

	public List<ConceptPojo> getTransformedConcepts() {
		return transformedConcepts;
	}

	public void setTransformedConcepts(List<ConceptPojo> transformedConcepts) {
		this.transformedConcepts = transformedConcepts;
	}

	public Map<String, String> getErrors() {
		return errors;
	}
	
}
