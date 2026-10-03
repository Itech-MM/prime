package org.flexitech.projects.erp.commons.validations;

import java.util.Map;

public class ValidationErrorResponse {

	private boolean success;
	private String message;
	private Map<String, String> fieldErrors;

	public ValidationErrorResponse(boolean success, String message, Map<String, String> fieldErrors) {
		this.success = success;
		this.message = message;
		this.fieldErrors = fieldErrors;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	public Map<String, String> getFieldErrors() {
		return fieldErrors;
	}
}