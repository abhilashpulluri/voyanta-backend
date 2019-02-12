package com.voyanta.dto;

import org.apache.commons.lang3.StringUtils;

public enum DstColumn implements Comparable<DstColumn> {

	ID(1L, "id", null),
	NAME(2L, "name", "AccountName"),
	REFERENCE(3L, "reference", "Reference"),
	CLIENT(4L, "client", "Client"),
	SUM(5L, "sum", "Values"),
	CATEGORY(6L, "category", "Category"),
	VALID(7L, "valid", null);

	private Long id;
	private String name;
	private String entryName;

	DstColumn(final Long id, final String name, final String entryName) {
		this.id = id;
		this.name = name;
		this.entryName = entryName;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEntryName() {
		return entryName;
	}

	public static DstColumn getByEntryName(final String name) {
		for (DstColumn column : DstColumn.values()) {
			if (StringUtils.equals(column.getEntryName(), name)) {
				return column;
			}
		}

		throw new IllegalArgumentException("Undefined column: " + name);
	}
}
