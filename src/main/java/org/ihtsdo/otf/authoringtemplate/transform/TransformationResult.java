package org.ihtsdo.otf.authoringtemplate.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;

public class TransformationResult {
	
	private List<ConceptPojo> concepts;
	private Map<String, String> failures;
	
	public TransformationResult() {
		concepts = new ArrayList<>();
	}
	
	public void addTransformedConcept(ConceptPojo transformed) {
		concepts.add(transformed);
	}

	public void setFailures(Map<String, String> failures) {
		this.failures = failures;
	}

	public List<ConceptPojo> getConcepts() {
		return concepts;
	}

	public void setConcepts(List<ConceptPojo> transformedConcepts) {
		this.concepts = transformedConcepts;
	}

	public Map<String, String> getFailures() {
		return failures;
	}
}
