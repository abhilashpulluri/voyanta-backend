package com.voyanta.exception;

public class ValidationException extends BaseException {

	private String field;

	public ValidationException(String code, String msg) {
		super(code, msg);
	}

	public ValidationException(String code, Object[] args, String msg) {
		super(code, args, msg);
	}

	public ValidationException(String field, String code, String msg) {
		super(code, msg);
		this.field = field;
	}

	public ValidationException(String field, String code, Object[] args, String msg) {
		super(code, args, msg);
		this.field = field;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

}
