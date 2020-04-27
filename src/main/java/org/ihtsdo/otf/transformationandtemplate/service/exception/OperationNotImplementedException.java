package org.ihtsdo.otf.transformationandtemplate.service.exception;

public class OperationNotImplementedException extends ServiceException {
	public OperationNotImplementedException() {
		super("The requested operation has not been implemented.");
	}

	public OperationNotImplementedException(String message) {
		super(message);
	}
}
