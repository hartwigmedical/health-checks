package com.hartwig.healthchecks.boggs.adapter;

import com.hartwig.healthchecks.boggs.flagstatreader.SambambaFlagStatParser;
import com.hartwig.healthchecks.boggs.healthcheck.MappingHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestastHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestatsExtractor;
import com.hartwig.healthchecks.boggs.io.PatientExtractor;
import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.util.CheckType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@ResourceWrapper(type = CheckType.BOGGS)
public class BoggsAdapter implements HealthCheckAdapter {

	private static Logger LOGGER = LogManager.getLogger(BoggsAdapter.class);

	private static final String IO_ERROR_MSG = "Got IO Exception with message: %s";

	public boolean runCheck(String runDirectory) {
		try {
			PatientExtractor dataExtractor = new PatientExtractor(new SambambaFlagStatParser());
			HealthChecker checker = new MappingHealthChecker(runDirectory, dataExtractor);

			PrestatsExtractor prestatsExtractor = new PrestatsExtractor();
			HealthChecker prestastHealthChecker = new PrestastHealthChecker(runDirectory, prestatsExtractor);
			return checker.isHealthy() && prestastHealthChecker.isHealthy();
		} catch (IOException e) {
			LOGGER.error(String.format(IO_ERROR_MSG, e.getMessage()));
		}
		return false;
	}
}
