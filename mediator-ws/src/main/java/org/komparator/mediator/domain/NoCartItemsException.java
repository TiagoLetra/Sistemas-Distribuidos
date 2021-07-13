package org.komparator.mediator.domain;

/** Exception used to signal a problem with the product quantity. */
public class NoCartItemsException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoCartItemsException() {
	}

	public NoCartItemsException(String message) {
		super(message);
	}

}
