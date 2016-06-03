package com.hartwig.healthchecks.boggs.healthcheck.prestast;

import static java.util.stream.Collectors.toCollection;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrestatsExtractor {

	private static final String SEPERATOR_REGEX = "\t";
	private static final String FAIL_ERROR = "FAIL";
	private static final String IO_ERROR_MSG = "Got IO Exception with message: %s";
	private static String DATA_FILE_NAME = "summary.txt";
	private static Logger LOGGER = LogManager.getLogger(PrestatsExtractor.class);

	public List<PrestatsData> extractFromRunDirectory(String runDirectory) throws IOException {
		List<Path> summaryFiles = Files.walk(new File(runDirectory).toPath())
				.filter(p -> p.getFileName().toString().startsWith(DATA_FILE_NAME)).sorted()
				.collect(toCollection(ArrayList<Path>::new));
		 List<PrestatsData> samplePrestatsData = new ArrayList<PrestatsData>();
				 
		for (Path path : summaryFiles) {
			try (BufferedReader reader = Files.newBufferedReader(path)) {
				List<String> prestatsErrors = reader.lines().filter(line -> line.contains(FAIL_ERROR))
						.map(line -> (line.split(SEPERATOR_REGEX)[1])).collect(toCollection(ArrayList<String>::new));
				if(!prestatsErrors.isEmpty()){
					PrestatsData prestatsData = new PrestatsData();
					prestatsData.setName(path.getParent().getFileName().toString());
					prestatsData.setPrestatsErrors(prestatsErrors);
					samplePrestatsData.add(prestatsData);
				}
			} catch (IOException ioException) {
				LOGGER.error(String.format(IO_ERROR_MSG, ioException.getMessage()));
			}
		}
		return samplePrestatsData;
	}
}
