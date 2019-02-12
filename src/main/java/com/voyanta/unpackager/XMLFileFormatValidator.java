package com.voyanta.unpackager;

import com.voyanta.exception.ValidationErrors;
import com.voyanta.exception.ValidationException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

public class XMLFileFormatValidator {

	private static final String EXTRACT_SUFFIX = "_EXTRACT";
	private static final String DATA_SUFFIX = "_data";

	private String filename;

	private XMLFileFormat xmlFileFormat;

	private String rootElementCodeName;
	private String dataElementCodeName;

	private XMLFileFormatValidator() {
	}

	public XMLFileFormatValidator(final InputStream inputStream, final String filename) throws ValidationException {
		try {
			this.filename = filename;
			this.xmlFileFormat = new XMLFileFormat(inputStream);
		} catch (Exception ex) {
			if (StringUtils.contains(ex.getMessage(), "Premature end of file")) {
				ValidationErrors.EMPTY_FILE_FOR_SUBMISSION.raise();
			} else if (ex instanceof SAXParseException
					&& (StringUtils.contains(ex.getMessage(), "XML document structures must start and end within the same entity")
					|| StringUtils.contains(ex.getMessage(), "end-tag"))) {
				ValidationErrors.SUBMISSION_FILE_XML_MISSING_END_ELEMENT.raiseWithArgs(
						Integer.valueOf(((SAXParseException) ex).getLineNumber()).toString());
			} else if (ex instanceof SAXParseException) {
				ValidationErrors.SUBMISSION_FILE_XML_INVALID_INPUT.raiseWithArgs(
						Integer.valueOf(((SAXParseException) ex).getLineNumber()).toString());
			} else {
				ValidationErrors.SUBMISSION_FILE_PARSING_FAILED.raise();
			}
		}
	}

	public XMLFileFormat validate() throws XPathExpressionException {

		this.shouldHaveAWellFormattedRootElementIfNotEmptyFile();
		this.shouldTheRootElementHaveADstVersion();
		this.shouldHaveAWellFormattedDataElement();
		this.shouldHaveOnlyOneDataElement();
		this.shouldTheDataElementEqualsToRootElement();
		this.shouldHaveAtLeastOneRow();
		this.shouldTheSubChildEqualsToRootElement();

		return this.xmlFileFormat;
	}

	private void shouldHaveAWellFormattedRootElementIfNotEmptyFile() throws XPathExpressionException {
		final Node rootElement = this.xmlFileFormat.computeNode("/*[1]");
		final String rootElementName = (rootElement == null) ? StringUtils.EMPTY : rootElement.getNodeName();

		if (StringUtils.isBlank(rootElementName) || !rootElementName.endsWith(XMLFileFormatValidator.EXTRACT_SUFFIX)) {
			ValidationErrors.SUBMISSION_FILE_XML_MISSING_ROOT_ELEMENT.raiseWithArgs(rootElementName);
		} else {
			this.rootElementCodeName = StringUtils.left(
					rootElementName, rootElementName.length() - XMLFileFormatValidator.EXTRACT_SUFFIX.length());
		}
	}

	private void shouldTheRootElementHaveADstVersion() throws XPathExpressionException {
		final String dstVersion = this.xmlFileFormat.computeString("/*[1]/@version").toUpperCase();

		if (false) {
			ValidationErrors.SUBMISSION_FILE_XLSX_INVALID_DST_VERSION.raiseWithArgs(dstVersion, filename);
		}

		this.xmlFileFormat.setDstVersion(dstVersion);
	}

	private void shouldHaveAWellFormattedDataElement() throws XPathExpressionException {
		final Node dataElement = this.xmlFileFormat.computeNode("/*/*[1]");
		final String dataElementName = (dataElement == null) ? StringUtils.EMPTY : dataElement.getNodeName();

		if (StringUtils.isBlank(dataElementName) || !dataElementName.endsWith(XMLFileFormatValidator.DATA_SUFFIX)) {
			ValidationErrors.SUBMISSION_FILE_XML_MISSING_DATA_ELEMENT.raiseWithArgs(dataElementName);
		} else {
			this.dataElementCodeName = StringUtils.left(
					dataElementName, dataElementName.length() - XMLFileFormatValidator.DATA_SUFFIX.length());
		}
	}

	private void shouldTheDataElementEqualsToRootElement() {
		if (StringUtils.isBlank(this.rootElementCodeName)
				|| StringUtils.isBlank(this.dataElementCodeName)
				|| !this.rootElementCodeName.equals(this.dataElementCodeName)) {
			ValidationErrors.SUBMISSION_FILE_XML_UNEXPECTED_DATA_ELEMENT.raiseWithArgs(
					this.rootElementCodeName, this.dataElementCodeName);
		}
	}

	private void shouldHaveOnlyOneDataElement() throws XPathExpressionException {
		final Long dataElementCount = this.xmlFileFormat.computeCount("/*/*");

		if (dataElementCount == null || dataElementCount > 1L) {
			ValidationErrors.SUBMISSION_FILE_XML_DATA_ELEMENT_EXCEED.raiseWithArgs(
					dataElementCount == null ? "0" : dataElementCount.toString());
		}
	}

	private void shouldHaveAtLeastOneRow() throws XPathExpressionException {
		final Long dataCount = this.xmlFileFormat.computeCount("/*/*");
		final Long rowCount = this.xmlFileFormat.computeCount("/*/*/*");
		final Long valueCount = this.xmlFileFormat.computeCount("/*/*/*/*[string-length(text()) > 0]");

		if (rowCount == null || rowCount < 1L
				|| dataCount == null || dataCount != 1L
				|| valueCount == null || valueCount < 1L) {
			ValidationErrors.EMPTY_FILE_FOR_SUBMISSION.raise();
		} else {
			this.xmlFileFormat.setRowCount(rowCount.intValue());
		}
	}

	private void shouldTheSubChildEqualsToRootElement() throws XPathExpressionException {
		final String expression = String.format("/*/*/*[not(self::%s)]", this.rootElementCodeName);
		final NodeList nodeList = this.xmlFileFormat.computeNodeSet(expression);
		final List<String> invalidNodeNames;

		if (nodeList.getLength() > 0) {
			invalidNodeNames = new ArrayList<>();

			for (int i = 0; i < nodeList.getLength() && i < 10; i++) {
				invalidNodeNames.add(nodeList.item(i).getNodeName());
			}

			ValidationErrors.SUBMISSION_FILE_XML_UNEXPECTED_SUBCHILD_ELEMENT.raiseWithArgs(
					this.rootElementCodeName, invalidNodeNames.toString());
		}
	}

}
