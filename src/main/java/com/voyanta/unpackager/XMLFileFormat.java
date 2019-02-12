package com.voyanta.unpackager;

import java.io.InputStream;
import java.io.Serializable;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLFileFormat implements Serializable {

	private Document xmlDocument;
	private XPath xPath;

	private String dstVersion;
	private Integer rowCount;

	private XMLFileFormat() {
	}

	public XMLFileFormat(InputStream inputStream) throws Exception {
		this.xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
		this.xPath = XPathFactory.newInstance().newXPath();
	}

	public String getDstVersion() {
		return dstVersion;
	}

	public void setDstVersion(String dstVersion) {
		this.dstVersion = dstVersion;
	}

	public Integer getRowCount() {
		return rowCount;
	}

	public void setRowCount(Integer rowCount) {
		this.rowCount = rowCount;
	}

	public Object compute(final String expression, QName returnType) throws XPathExpressionException {
		return this.xPath.compile(expression).evaluate(this.xmlDocument, returnType);
	}

	public Node computeNode(final String expression) throws XPathExpressionException {
		return (Node) this.compute(expression, XPathConstants.NODE);
	}

	public Long computeCount(final String expression) throws XPathExpressionException {
		return ((Double) this.compute(String.format("count(%s)", expression), XPathConstants.NUMBER)).longValue();
	}

	public String computeString(final String expression) throws XPathExpressionException {
		return (String) this.compute(expression, XPathConstants.STRING);
	}

	public NodeList computeNodeSet(final String expression) throws XPathExpressionException {
		return (NodeList) this.compute(expression, XPathConstants.NODESET);
	}

}
