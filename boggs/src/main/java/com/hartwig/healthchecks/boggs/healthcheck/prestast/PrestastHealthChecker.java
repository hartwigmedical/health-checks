package com.hartwig.healthchecks.boggs.healthcheck.prestast;

import com.hartwig.healthchecks.boggs.model.PrestatsData;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class PrestastHealthChecker implements HealthChecker {

	private static Logger LOGGER = LogManager.getLogger(PrestastHealthChecker.class);
	private static final String FOUND_FAILS_MSG = "NOT OK: Found Errors coming from the The Prestats file %s : %s  ";

	private String runDirectory;

	private PrestatsExtractor dataExtractor;

	public PrestastHealthChecker(String runDirectory, PrestatsExtractor dataExtractor) {
		this.runDirectory = runDirectory;
		this.dataExtractor = dataExtractor;
	}

	@Override
	public boolean isHealthy() throws IOException {
		PrestatsData prestatsErrors = dataExtractor.extractFromRunDirectory(runDirectory);
		prestatsErrors.getSummary().forEach( (k, v) -> {
			LOGGER.info(String.format(FOUND_FAILS_MSG, k, v));
		});

		Report report = JsonReport.getInstance();
		report.addReportData(prestatsErrors);

		return true;
	}
}
