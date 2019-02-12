package com.voyanta.dto;

import java.io.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidFileDTO {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidFileDTO.class);

	private String name;
	private Long size;
	private File file;
	private String encoding;
	private boolean empty;

	public ValidFileDTO(final File file, final String name) {
		this.file = file;
		this.name = name;
		this.size = file.length();
		this.empty = this.size.equals(0L);
	}

	public String getName() {
		return this.name;
	}

	public Long getSize() {
		return this.size;
	}

	public InputStream getInputStream() throws FileNotFoundException {
		return new FileInputStream(this.file);
	}

	public void setEncoding(final String encoding) {
		this.encoding = encoding;
	}

	public File getFile() {
		return this.file;
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
