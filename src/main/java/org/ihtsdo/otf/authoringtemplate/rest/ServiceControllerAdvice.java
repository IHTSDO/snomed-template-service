package org.ihtsdo.otf.authoringtemplate.rest;

import org.ihtsdo.otf.authoringtemplate.rest.error.InputError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ServiceControllerAdvice {

	@ResponseBody
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	InputError illegalArgumentExceptionHandler(IllegalArgumentException e) {
		return new InputError(e.getMessage());
	}

	@ResponseBody
	@ExceptionHandler(InputError.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	InputError inputErrorHandler(InputError e) {
		return e;
	}

}
