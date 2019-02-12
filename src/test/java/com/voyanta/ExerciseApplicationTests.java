package com.voyanta;

import com.voyanta.dto.ValidFileDTO;
import com.voyanta.unpackager.XMLFileUnpackager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ExerciseApplicationTests {

	private static final String FILE_DIRECTORY = "/submission/";

	@Autowired
	private CsvParser csvParser;

	private static final Logger LOG = LoggerFactory.getLogger(ExerciseApplicationTests.class);

	@Test
	public void compareGeneratedFiles() throws Exception {
		final File file = new File(getClass().getResource(FILE_DIRECTORY + "inputFile.xml").toURI());
		final ValidFileDTO inputFileDTO = new ValidFileDTO(file, "inputFile");
		final XMLFileUnpackager fileUnpackager = new XMLFileUnpackager(inputFileDTO);
		final ValidFileDTO fileDTO;
		final long startTime;
		final long duration;

		startTime = System.currentTimeMillis();

		fileDTO = fileUnpackager.splitFile();

		duration = System.currentTimeMillis() - startTime;

		try (InputStream actualStream = fileDTO.getInputStream()) {
			assertTrue("expected and actual result don't match",
					csvParser.compareResult(FILE_DIRECTORY + "outputFile.csv", actualStream));
		} catch (IOException e) {
			LOG.error("Error in parsing the result file.", e);
		}
		LOG.info("Export time : {} ms", duration);

		assertTrue("Export is too slow, find a way to process below 1s", duration < 1000);
	}
}
