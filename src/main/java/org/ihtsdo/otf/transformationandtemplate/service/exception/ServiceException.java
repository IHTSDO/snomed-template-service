package org.ihtsdo.otf.transformationandtemplate.service.exception;

public class ServiceException extends Exception {

	private static final long serialVersionUID = 5611174709018541114L;

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
