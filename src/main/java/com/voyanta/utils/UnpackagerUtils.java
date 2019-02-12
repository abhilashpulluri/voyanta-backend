package com.voyanta.utils;

import com.voyanta.dto.DstColumn;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class UnpackagerUtils {

	public static String createCsvRowWithValueMatchedToColumn(final List<DstColumn> dstColumns, final Map<DstColumn, String> record) {
		final StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < dstColumns.size(); i++) {
			final DstColumn dstColumn = dstColumns.get(i);

			if (i < dstColumns.size()) {
				final String value = record.get(dstColumn);

				stringBuilder.append(StringEscapeUtils.escapeCsv(value != null ? value : ""));
			}
			if (i != (dstColumns.size() - 1)) {
				stringBuilder.append(",");
			}
		}

		return stringBuilder.toString();
	}

	public static String createCsvRowForDstColumnNames() {
		final StringBuilder stringBuilder = new StringBuilder();

		List<DstColumn> dstColumns = Arrays.asList(DstColumn.values());

		for (int i = 0; i < dstColumns.size(); i++) {
			final DstColumn dstColumn = dstColumns.get(i);
			final String columnName = dstColumn.getName();

			if (i < dstColumns.size()) {
				stringBuilder.append("\"").append(columnName).append("\"");
			}
			if (i != (dstColumns.size() - 1)) {
				stringBuilder.append(",");
			}
		}

		return stringBuilder.toString();
	}

	public static String createCsvRow(final Integer maxColumns, final String... values) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < maxColumns; i++) {
			if (i < values.length && StringUtils.isNotBlank(values[i])) {
				stringBuilder.append("\"").append(values[i]).append("\"");
			}
			if (i != (maxColumns - 1)) {
				stringBuilder.append(",");
			}
		}

		return stringBuilder.toString();
	}
}
