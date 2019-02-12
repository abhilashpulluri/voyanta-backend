package com.voyanta.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.voyanta.dto.ValidFileDTO;
import com.voyanta.service.FileStorage;
import com.voyanta.unpackager.XMLFileUnpackager;

@RestController
public class ConvertXMLToCSVFile {

	private static final Logger LOGGER = LoggerFactory.getLogger(XMLFileUnpackager.class);

	@Autowired
	FileStorage fileStorage;

	@PostMapping("/uploadFile")
	public ResponseEntity<?> getCSVFile(@RequestParam("file") MultipartFile xmlFile, HttpServletResponse response) {

		try {

			fileStorage.store(xmlFile);
			final File file = new File(fileStorage.loadFile(xmlFile.getOriginalFilename()).getURI());
			final ValidFileDTO inputFileDTO = new ValidFileDTO(file, "inputFile");
			final XMLFileUnpackager fileUnpackager = new XMLFileUnpackager(inputFileDTO);
			final ValidFileDTO fileDTO = fileUnpackager.splitFile();

			fileStorage.deleteAll();
			return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/csv"))
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"attachment; filename=\"" + fileDTO.getFile().getAbsolutePath() + "\"")
					.body("");

		} catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}

		return null;

	}

}
