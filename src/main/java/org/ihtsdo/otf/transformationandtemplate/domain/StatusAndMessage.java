package org.ihtsdo.otf.transformationandtemplate.domain;

import org.ihtsdo.otf.transformationandtemplate.service.template.TransformationStatus;

public class StatusAndMessage {

	private TransformationStatus status;
	private String message;

	public StatusAndMessage() {
	}

	public StatusAndMessage(TransformationStatus status) {
		this.status = status;
	}

	public StatusAndMessage(TransformationStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	public TransformationStatus getStatus() {
		return status;
	}

	public void setStatus(TransformationStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return status + (message == null ? "" : ", message='" + message + '\'');
	}
}
