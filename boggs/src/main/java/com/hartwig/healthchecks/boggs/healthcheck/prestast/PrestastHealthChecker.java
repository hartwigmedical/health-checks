package com.hartwig.healthchecks.boggs.healthcheck.prestast;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.util.BaseReport;

public class PrestastHealthChecker implements HealthChecker {

	private static Logger LOGGER = LogManager.getLogger(PrestastHealthChecker.class);
	private static final String FAIL_ERROR = "FAIL";
	private static final String FOUND_FAILS_MSG = "NOT OK: %s has status FAIL in file %s ";

	private String runDirectory;

	private PrestatsExtractor dataExtractor;

	public PrestastHealthChecker(String runDirectory, PrestatsExtractor dataExtractor) {
		this.runDirectory = runDirectory;
		this.dataExtractor = dataExtractor;
	}

	@Override
	public BaseReport runCheck() throws IOException {
		PrestatsReport prestatsReport = dataExtractor.extractFromRunDirectory(runDirectory);
		prestatsReport.getSummary().forEach((v) -> {
			if (v.getStatus().equalsIgnoreCase(FAIL_ERROR)) {
				LOGGER.info(String.format(FOUND_FAILS_MSG, v.getCheckName(), v.getFile()));
			}
		});
		return prestatsReport;
	}
}
