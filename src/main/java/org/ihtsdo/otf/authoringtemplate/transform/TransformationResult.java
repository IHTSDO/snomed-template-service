package org.ihtsdo.otf.authoringtemplate.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;

public class TransformationResult {
	
	private List<ConceptPojo> concepts;
	private Map<String, String> failures;
	
	public TransformationResult() {
		concepts = new ArrayList<>();
	}
	
	public void addTransformedConcept(ConceptPojo transformed) {
		concepts.add(transformed);
	}

	public void setErrors(Map<String, String> failures) {
		this.failures = failures;
	}

	public List<ConceptPojo> getTransformedConcepts() {
		return concepts;
	}

	public void setTransformedConcepts(List<ConceptPojo> transformedConcepts) {
		this.concepts = transformedConcepts;
	}

	public Map<String, String> getFailures() {
		return failures;
	}
}
