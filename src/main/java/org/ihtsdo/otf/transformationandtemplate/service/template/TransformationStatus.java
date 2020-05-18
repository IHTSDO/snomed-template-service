package org.ihtsdo.otf.transformationandtemplate.service.template;

public enum TransformationStatus {

	QUEUED, RUNNING, FAILED(true), COMPLETED_WITH_FAILURE(true), COMPLETED(true);

	boolean endState;

	TransformationStatus() {
	}

	TransformationStatus(boolean endState) {
		this.endState = endState;
	}

	public boolean isEndState() {
		return endState;
	}
}
