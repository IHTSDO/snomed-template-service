package org.ihtsdo.otf.authoringtemplate.transform;

import java.util.Set;

import org.ihtsdo.otf.authoringtemplate.service.Constants;

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TemplateTransformRequest [");
		if (conceptsToTransform != null)
			builder.append("conceptsToTransform=").append(conceptsToTransform).append(", ");
		if (sourceTemplate != null)
			builder.append("sourceTemplate=").append(sourceTemplate).append(", ");
		if (inactivationReason != null)
			builder.append("inactivationReason=").append(inactivationReason);
		builder.append("]");
		return builder.toString();
	}
}
