package org.komparator.mediator.domain;

/** Exception used to signal a problem with the product quantity. */
public class NotItemException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotItemException() {
	}

	public NotItemException(String message) {
		super(message);
	}

}
