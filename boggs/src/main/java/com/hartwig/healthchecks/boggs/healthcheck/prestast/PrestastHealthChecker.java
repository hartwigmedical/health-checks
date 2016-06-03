package com.hartwig.healthchecks.boggs.healthcheck.prestast;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hartwig.healthchecks.common.checks.HealthChecker;

public class PrestastHealthChecker implements HealthChecker {

	private static Logger LOGGER = LogManager.getLogger(PrestastHealthChecker.class);
	private static final String FOUND_FAILS_MSG = "NOT OK: Found %s Errors in The Prestats folder %s : %s  ";
	private static final String IO_ERROR_MSG = "Got IO Exception with message: %s";

	private String runDirectory;

	private PrestatsExtractor dataExtractor;

	public PrestastHealthChecker(String runDirectory, PrestatsExtractor dataExtractor) {
		this.runDirectory = runDirectory;
		this.dataExtractor = dataExtractor;
	}

	@Override
	public boolean isHealthy() {
		List<PrestatsData> prestatsErrors;
		try {
			prestatsErrors = dataExtractor.extractFromRunDirectory(runDirectory);
			if (prestatsErrors != null && !prestatsErrors.isEmpty()) {
				;
				prestatsErrors.forEach((prestatsError) -> {
					LOGGER.info(String.format(FOUND_FAILS_MSG, prestatsError.getPrestatsErrors().size(),
							prestatsError.name, prestatsError.prestatsErrors));
				});

				return false;
			}
		} catch (IOException ioException) {
			LOGGER.error(String.format(IO_ERROR_MSG, ioException.getMessage()));
		}
		return true;
	}
}
