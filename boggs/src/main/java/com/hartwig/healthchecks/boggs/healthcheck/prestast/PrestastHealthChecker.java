package com.hartwig.healthchecks.boggs.healthcheck.prestast;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hartwig.healthchecks.common.checks.HealthChecker;

public class PrestastHealthChecker implements HealthChecker {

	private static Logger LOGGER = LogManager.getLogger(PrestastHealthChecker.class);
	private static final String FOUND_FAILS_MSG = "NOT OK: Found %s Errors in The Prestats folder %s : %s  ";

	private String runDirectory;

	private PrestatsExtractor dataExtractor;

	public PrestastHealthChecker(String runDirectory, PrestatsExtractor dataExtractor) {
		this.runDirectory = runDirectory;
		this.dataExtractor = dataExtractor;
	}

	@Override
	public boolean isHealthy() throws IOException {
		List<PrestatsData> prestatsErrors;
		prestatsErrors = dataExtractor.extractFromRunDirectory(runDirectory);
		if (prestatsErrors != null && !prestatsErrors.isEmpty()) {
			;
			prestatsErrors.forEach((prestatsError) -> {
				LOGGER.info(String.format(FOUND_FAILS_MSG, prestatsError.getPrestatsErrors().size(), prestatsError.name,
						prestatsError.prestatsErrors));
			});
			return false;
		}

		return true;
	}
}
