package org.komparator.mediator.domain;

/** Exception used to signal a problem with the product quantity. */
public class CCInvalidException extends Exception {

	private static final long serialVersionUID = 1L;

	public CCInvalidException() {
	}

	public CCInvalidException(String message) {
		super(message);
	}

}
