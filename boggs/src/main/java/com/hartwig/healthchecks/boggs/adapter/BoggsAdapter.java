package com.hartwig.healthchecks.boggs.adapter;

import com.hartwig.healthchecks.boggs.flagstatreader.SambambaFlagStatParser;
import com.hartwig.healthchecks.boggs.healthcheck.MappingHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestastHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestatsExtractor;
import com.hartwig.healthchecks.boggs.io.PatientExtractor;
import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.HealthChecker;

public class BoggsAdapter implements HealthCheckAdapter {

	public boolean runCheck(String runDirectory) {
		PatientExtractor dataExtractor = new PatientExtractor(new SambambaFlagStatParser());
		HealthChecker checker = new MappingHealthChecker(runDirectory,dataExtractor);
		
		PrestatsExtractor prestatsExtractor = new PrestatsExtractor();
		HealthChecker prestastHealthChecker = new PrestastHealthChecker(runDirectory,prestatsExtractor);
		
		return checker.isHealthy() && prestastHealthChecker.isHealthy();
	}
}
