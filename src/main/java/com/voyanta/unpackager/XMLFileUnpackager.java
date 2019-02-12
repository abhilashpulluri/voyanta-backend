package com.voyanta.unpackager;

import com.voyanta.dto.DstColumn;
import com.voyanta.dto.ValidFileDTO;
import com.voyanta.exception.ValidationErrors;
import com.voyanta.utils.UnpackagerUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLFileUnpackager extends AbstractFileUnpackager {

	private static final Logger LOGGER = LoggerFactory.getLogger(XMLFileUnpackager.class);
	private static final Integer MAX_RANGE_COUNT = 5;
	private static final String CLIENT_CODE = "CAO";

	private ValidFileDTO fileDTO;
	private XMLFileFormat xmlFileFormat;

	public XMLFileUnpackager(final ValidFileDTO fileDTO) {
		this.fileDTO = fileDTO;
	}

	@Override
	public ValidFileDTO splitFile() throws Exception {
		ValidFileDTO csvFileDTO = null;
		File csvFile;

		try {
			csvFile = Files.createTempFile("", ".csv").toFile();
			this.xmlFileFormat = new XMLFileFormatValidator(this.fileDTO.getInputStream(), this.fileDTO.getName()).validate();
			this.convertAndSave(csvFile);
			csvFileDTO = new ValidFileDTO(csvFile, "CsvExport");
			csvFileDTO.setEncoding(StandardCharsets.UTF_8.name());

			LOGGER.info("Export file name={}", fileDTO.getName());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			trackDetailsForError(e.getMessage(), ValidationErrors.SUBMISSION_FILE_PARSING_FAILED.getErrorMessageCode(),
					fileDTO.getName());
		}

		return csvFileDTO;
	}

	/**
	 * Exercise 2 : fix convertAndSave() method to make the UT pass
	 * Do not modify any other code than this method.
	 */
	private void convertAndSave(File csvFile) throws Exception {
		final String dstVersion = this.xmlFileFormat.getDstVersion();
		final Integer rowCount = this.xmlFileFormat.getRowCount();
		final Integer maxColumnCount = DstColumn.values().length;
		Set<String> invalidColumnHeader = new HashSet<>();
		Map<String, String> sumCache = new HashMap<>();

		try (final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile, true), StandardCharsets.UTF_8))) {
			bufferedWriter.write(UnpackagerUtils.createCsvRow(maxColumnCount, "", "CSV export", "", dstVersion));
			bufferedWriter.newLine();
			bufferedWriter.write(UnpackagerUtils.createCsvRowForDstColumnNames());
			bufferedWriter.newLine();

			for (int i = 0; i < rowCount; i++) {
				final List<Map<String, String>> records = this.readRange(i);

				for (Map<String, String> record : records) {
					Map<DstColumn, String> validRecords = new HashMap<>();
					String values;
					String clientName;

					for (Map.Entry<String, String> entry : record.entrySet()) {
						final DstColumn dstColumn;

						try {
							dstColumn = DstColumn.getByEntryName(entry.getKey());
							validRecords.put(dstColumn, entry.getValue());
						} catch (Exception ex) {
							invalidColumnHeader.add(entry.getKey());
						}
					}

					values = validRecords.get(DstColumn.SUM.getId());

					if (Objects.nonNull(values)) {
						String sum = sumCache.get(values);

						if (Objects.isNull(sum)) {
							sum = addValues(values);
							sumCache.put(values, sum);
						}

						validRecords.put(DstColumn.SUM, sum);
					}

					clientName = validRecords.get(DstColumn.CLIENT);

					if (Objects.nonNull(clientName) && StringUtils.contains(clientName, CLIENT_CODE)) {
						validRecords.put(DstColumn.VALID, "1");
					}

					validRecords.put(DstColumn.ID, String.valueOf(i + 1));

					bufferedWriter.write(UnpackagerUtils.createCsvRowWithValueMatchedToColumn(Arrays.asList(DstColumn.values()), validRecords));
					bufferedWriter.newLine();

					i++;
				}
			}
		}

		if (!invalidColumnHeader.isEmpty()) {
			ValidationErrors.SUBMISSION_FILE_INVALID_HEADERS.raise(invalidColumnHeader.toString());
		}

		LOGGER.info("Generating file {} from XML", csvFile.getAbsolutePath());
	}

	/**
	 * Simple method to sum numbers from a String comma separated "x,y,z,..." => x + y + z + ...
	 * @param values
	 * @return
	 */
	private String addValues(final String values) {
		try {
			// simulate long calculation
			TimeUnit.MILLISECONDS.sleep(100);
		} catch (InterruptedException e) {
		}

		return String.valueOf(Arrays.stream(values.split(",")).mapToInt(Integer::parseInt).sum());

	}

	private List<Map<String, String>> readRange(final Integer rowIndexStart) {
		final String expression = String.format("/*/*/*[position() >= %s and position() <= %s]",
				rowIndexStart + 1, rowIndexStart + MAX_RANGE_COUNT);
		final NodeList nodeList;
		List<Map<String, String>> records = new ArrayList<>();

		try {
			nodeList = this.xmlFileFormat.computeNodeSet(expression);

			for (int i = 0; i < nodeList.getLength(); i++) {
				records.add(this.readRow(nodeList.item(i).getChildNodes()));
			}

			return records;
		} catch (Exception e) {
			ValidationErrors.SUBMISSION_FILE_PARSING_FAILED.raise("Not able to retrieve the rows");
		}

		return null;
	}

	private Map<String, String> readRow(final NodeList nodeList) {
		Map<String, String> record = new HashMap<>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			if (!nodeList.item(i).getNodeName().equalsIgnoreCase("VoyantaTags")
					&& !nodeList.item(i).getNodeName().equalsIgnoreCase("#text")) {
				final Node node = nodeList.item(i);

				record.put(node.getNodeName(), StringEscapeUtils.unescapeXml(node.getTextContent()));
			}
		}

		return record;
	}
}
