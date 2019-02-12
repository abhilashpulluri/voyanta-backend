package com.voyanta.exception;

/**
 * Has to be used in any unexpected cases on backend-side which are not depend on income requests.
 *
 */
public class UnexpectedException extends BaseException {

	private static final long serialVersionUID = 1L;
	private static final String UNEXPECTED_ERROR_OCCURED = "Error.unexpected.occurred";
	
	public UnexpectedException(String msg) {
		super(UNEXPECTED_ERROR_OCCURED, msg);
	}

	public UnexpectedException(Throwable cause) {
		super(cause);
	}

}
