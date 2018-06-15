package org.ihtsdo.otf.authoringtemplate.service;

import java.util.Set;

public class TemplateTransformRequest {

	private Set<String> conceptsToTransform;
	private String sourceTemplate;
	private String inactivationReason;
	
	public void setConceptsToTransform(Set<String> conceptsToTransform) {
		this.conceptsToTransform = conceptsToTransform;
	}

	public void setSourceTemplate(String sourceTemplate) {
		this.sourceTemplate = sourceTemplate;
	}

	public void setInactivationReason(String inactivationReason) {
		this.inactivationReason = inactivationReason;
	}
	
	public String getSourceTemplate() {
		return this.sourceTemplate;
	}

	public Set<String> getConceptsToTransform() {
		return this.conceptsToTransform;
	}

	public String getInactivationReason() {
		if (inactivationReason == null || inactivationReason.isEmpty()) {
			return Constants.NONCONFORMANCE;
		}
		return this.inactivationReason;
	}

}
