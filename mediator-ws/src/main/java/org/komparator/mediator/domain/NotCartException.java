package org.komparator.mediator.domain;

/** Exception used to signal a problem with the product quantity. */
public class NotCartException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotCartException() {
	}

	public NotCartException(String message) {
		super(message);
	}

}
