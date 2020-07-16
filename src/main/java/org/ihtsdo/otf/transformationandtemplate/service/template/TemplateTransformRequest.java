package org.ihtsdo.otf.transformationandtemplate.service.template;

import java.util.Set;

import org.ihtsdo.otf.transformationandtemplate.service.ConstantStrings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplateTransformRequest {
	
	private Set<String> conceptsToTransform;
	private String sourceTemplate;
	private String destinationTemplate;
	private String inactivationReason;
	private boolean logicalTransform = true;
	private boolean lexicalTransform = true;
	
	public TemplateTransformRequest() {
	}
	
	public TemplateTransformRequest(String source, String destination) {
		this.sourceTemplate = source;
		this.destinationTemplate = destination;
	}
	
	public void setConceptsToTransform(Set<String> conceptsToTransform) {
		this.conceptsToTransform = conceptsToTransform;
	}

	public void setInactivationReason(String inactivationReason) {
		this.inactivationReason = inactivationReason;
	}
	
	public String getSourceTemplate() {
		return this.sourceTemplate;
	}
	
	public String getDestinationTemplate() {
		return destinationTemplate;
	}
	
	public void setDestinationTemplate(String destination) {
		this.destinationTemplate = destination;
	}

	public Set<String> getConceptsToTransform() {
		return this.conceptsToTransform;
	}

	public String getInactivationReason() {
		if (inactivationReason == null || inactivationReason.isEmpty()) {
			return ConstantStrings.NONCONFORMANCE;
		}
		return this.inactivationReason;
	}
	
	public boolean isLogicalTransform() {
		return logicalTransform;
	}

	public void setLogicalTransform(boolean logicalTransform) {
		this.logicalTransform = logicalTransform;
	}

	public boolean isLexicalTransform() {
		return lexicalTransform;
	}

	public void setLexicalTransform(boolean lexicalTransform) {
		this.lexicalTransform = lexicalTransform;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TemplateTransformRequest [");
		if (conceptsToTransform != null)
			builder.append("conceptsToTransform=").append(conceptsToTransform).append(", ");
		if (sourceTemplate != null)
			builder.append("sourceTemplate=").append(sourceTemplate).append(", ");
		if (destinationTemplate != null)
			builder.append("destinationTemplate=").append(destinationTemplate).append(", ");
		if (inactivationReason != null)
			builder.append("inactivationReason=").append(inactivationReason).append(", ");
		builder.append("logicalTransform=").append(logicalTransform).append(", lexicalTransform=")
				.append(lexicalTransform).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conceptsToTransform == null) ? 0 : conceptsToTransform.hashCode());
		result = prime * result + ((inactivationReason == null) ? 0 : inactivationReason.hashCode());
		result = prime * result + (lexicalTransform ? 1231 : 1237);
		result = prime * result + (logicalTransform ? 1231 : 1237);
		result = prime * result + ((sourceTemplate == null) ? 0 : sourceTemplate.hashCode());
		result = prime * result + ((destinationTemplate == null) ? 0 : destinationTemplate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TemplateTransformRequest other = (TemplateTransformRequest) obj;
		if (conceptsToTransform == null) {
			if (other.conceptsToTransform != null)
				return false;
		} else if (!conceptsToTransform.equals(other.conceptsToTransform))
			return false;
		if (inactivationReason == null) {
			if (other.inactivationReason != null)
				return false;
		} else if (!inactivationReason.equals(other.inactivationReason))
			return false;
		if (lexicalTransform != other.lexicalTransform)
			return false;
		if (logicalTransform != other.logicalTransform)
			return false;
		if (sourceTemplate == null) {
			if (other.sourceTemplate != null)
				return false;
		} else if (!sourceTemplate.equals(other.sourceTemplate))
			return false;
		if (destinationTemplate == null) {
			if (other.destinationTemplate != null)
				return false;
		} else if (!destinationTemplate.equals(other.destinationTemplate))
			return false;
		return true;
	}
}
