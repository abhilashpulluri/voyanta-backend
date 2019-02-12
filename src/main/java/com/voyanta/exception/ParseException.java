package com.voyanta.exception;

public class ParseException extends BaseException {

	public ParseException(String code, String msg) {
		super(code, msg);
	}

	public ParseException(String code, String msg, Throwable cause) {
		super(code, msg, cause);
	}
}
