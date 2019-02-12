package com.voyanta;

import com.opencsv.CSVReader;
import com.voyanta.exception.ParseException;
import com.voyanta.utils.StreamUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CsvParser {

	private static final Logger LOG = LoggerFactory.getLogger(CsvParser.class);

	private static final char SEPARATOR = ',';

	public boolean compareResult(final String expectedFile, final InputStream actualStream) {
		try (InputStream expectedStream = getClass().getResource(expectedFile).openStream()) {
			final List<Map<String, String>> expectedResult = parse(expectedStream);
			final List<Map<String, String>> actualResult = parse(actualStream);

			if (actualResult.size() != expectedResult.size()) {
				LOG.error("TenancySchedule parsing row numbers don't match the expected");
				return false;
			}

			for (int i = 0; i < expectedResult.size(); i++) {
				final Map<String, String> expected = expectedResult.get(i);
				final Map<String, String> actual = actualResult.get(i);

				for (String header : expected.keySet()) {
					if (!expected.get(header).equals(actual.get(header))) {
						LOG.error("expected " + header + ":");
						LOG.error(expected.get(header).toString());
						LOG.error("actual " + header + ":");
						LOG.error(actual.get(header).toString());
						return false;
					}
				}
			}
		} catch (IOException e) {
			LOG.error("Error in parsing the expected result file : " + expectedFile, e);
			return false;
		}

		return true;
	}

	private List<Map<String, String>> parse(final InputStream dataStream) throws ParseException {
		try (CSVReader subtotalReader = new CSVReader(StreamUtils.getBufferedReader(dataStream, StreamUtils.UTF8), SEPARATOR)) {
			final List<Map<String, String>> result = new ArrayList<>();
			String[] subLine;

			//Parse headers
			parseRow(subtotalReader);

			final List<String> headers = Arrays.asList(parseRow(subtotalReader));

			while ((subLine = subtotalReader.readNext()) != null) {
				final Map<String, String> row = new LinkedHashMap<>();

				for (int i = 0; i < headers.size(); i++) {
					row.put(headers.get(i), subLine[i]);
				}

				result.add(row);
			}

			return result;
		} catch (Exception e) {
			throw new ParseException("DST.parse.malformed.csv", e.getMessage(), e);
		}
	}

	private String[] parseRow(final CSVReader reader) throws IOException {
		return reader.readNext();
	}

}
