package org.komparator.mediator.domain;

/** Exception used to signal a problem with the product quantity. */
public class NotEnoughProductsException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotEnoughProductsException() {
	}

	public NotEnoughProductsException(String message) {
		super(message);
	}

}
