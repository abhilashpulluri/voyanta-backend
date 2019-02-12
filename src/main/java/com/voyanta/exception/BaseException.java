package com.voyanta.exception;

public abstract class BaseException extends RuntimeException {

	private String errorCode;
	private Object[] args;

	public BaseException(Throwable cause) {
		super(cause);
	}

	protected BaseException(String code, String msg) {
		this(code, null, msg);
	}

	protected BaseException(String code, Object[] args, String msg) {
		super(msg);
		this.errorCode = code;
		this.args = args;
	}

	protected BaseException(String code, String msg, Throwable cause) {
		this(code, null, msg, cause);
	}

	protected BaseException(String code, Object[] args, String msg, Throwable cause) {
		super(msg, cause);
		errorCode = code;
		this.args = args;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}
}
