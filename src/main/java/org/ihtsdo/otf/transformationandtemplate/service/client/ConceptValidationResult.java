package org.ihtsdo.otf.transformationandtemplate.service.client;

import static java.lang.String.format;

public class ConceptValidationResult {

	private String componentId;
	private String conceptId;
	private String message;
	private Severity severity;
	private boolean published;
	private boolean ignorePublishedCheck;
	private ValidationResultComponent component;

	public enum Severity {
		ERROR, WARNING
	}

	public String getComponentId() {
		return componentId;
	}

	public String getConceptId() {
		return conceptId;
	}

	public String getMessage() {
		return message;
	}

	public Severity getSeverity() {
		return severity;
	}

	public boolean isPublished() {
		return published;
	}

	public boolean isIgnorePublishedCheck() {
		return ignorePublishedCheck;
	}

	public ValidationResultComponent getComponent() {
		return component;
	}

	private static class ValidationResultComponent {
		private String id;
		private String moduleId;
		private boolean active;
		private boolean published;
		private boolean released;

		public String getId() {
			return id;
		}

		public String getModuleId() {
			return moduleId;
		}

		public boolean isActive() {
			return active;
		}

		public boolean isPublished() {
			return published;
		}

		public boolean isReleased() {
			return released;
		}
	}

	@Override
	public String toString() {
		return format("%s for Component %s in Concept %s: %s. ", severity, componentId, conceptId, message);
	}
}
