package org.flexitech.projects.erp.commons.exceptions;

public class CartItemNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6636530154402333068L;

	public CartItemNotFoundException(String message) {
		super(message);
	}
}
