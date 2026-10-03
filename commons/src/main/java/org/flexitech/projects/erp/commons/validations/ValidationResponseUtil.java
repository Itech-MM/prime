package org.flexitech.projects.erp.commons.validations;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public final class ValidationResponseUtil {

	private ValidationResponseUtil() {
	}

	public static ResponseEntity<ValidationErrorResponse> badRequest(BindingResult result) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		for (FieldError error : result.getFieldErrors()) {
			fieldErrors.put(error.getField(), error.getDefaultMessage());
		}
		ValidationErrorResponse body = new ValidationErrorResponse(false, "Please fix the highlighted fields", fieldErrors);
		return ResponseEntity.badRequest().body(body);
	}
}