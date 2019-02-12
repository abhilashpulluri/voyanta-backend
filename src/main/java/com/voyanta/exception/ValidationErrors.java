package com.voyanta.exception;

public enum ValidationErrors {

	EMPTY_FILE_FOR_SUBMISSION("Submission.empty.file"),
	SUBMISSION_FILE_INVALID_HEADERS("Submission.file.invalid.headers"),
	SUBMISSION_FILE_PARSING_FAILED("Submission.validation.file.parsing.failed"),

	SUBMISSION_FILE_XML_MISSING_ROOT_ELEMENT("Submission.validation.xml.root.missing"),
	SUBMISSION_FILE_XML_MISSING_END_ELEMENT("Submission.validation.xml.missing.end.element"),
	SUBMISSION_FILE_XML_MISSING_DATA_ELEMENT("Submission.validation.xml.data.missing"),
	SUBMISSION_FILE_XML_UNEXPECTED_DATA_ELEMENT("Submission.validation.xml.unexpected.data.element"),
	SUBMISSION_FILE_XML_UNEXPECTED_SUBCHILD_ELEMENT("Submission.validation.xml.unexpected.subchild.element"),
	SUBMISSION_FILE_XML_DATA_ELEMENT_EXCEED("Submission.validation.xml.data.element.exceed"),
	SUBMISSION_FILE_XML_INVALID_INPUT("Submission.validation.xml.invalid.input"),
	SUBMISSION_FILE_XLSX_INVALID_DST_VERSION("Submission.file.unknown_dst_version");

	private String errorMessageCode;

	private ValidationErrors(final String errorMessageCode) {
		this.errorMessageCode = errorMessageCode;
	}

	public void raise(final String message) throws BaseException {
		throw new ValidationException(this.errorMessageCode, message);
	}

	public void raise(final String fieldName, final String message) throws BaseException {
		throw new ValidationException(fieldName, this.errorMessageCode, message);
	}

	public void raiseWithArgs(String... params) throws BaseException {
		throw new ValidationException(this.errorMessageCode, params, null);
	}

	public void raise() throws BaseException {
		throw new ValidationException(this.errorMessageCode, null);
	}

	public String getErrorMessageCode() {
		return errorMessageCode;
	}
}
