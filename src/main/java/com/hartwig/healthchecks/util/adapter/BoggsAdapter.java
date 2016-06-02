package com.hartwig.healthchecks.util.adapter;

import com.hartwig.healthchecks.boggs.PatientData;
import com.hartwig.healthchecks.boggs.flagstatreader.SambambaFlagStatParser;
import com.hartwig.healthchecks.boggs.healthcheck.HealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.MappingHealthChecker;
import com.hartwig.healthchecks.boggs.io.PatientExtractor;
import java.io.IOException;

public class BoggsAdapter implements HealthCheckAdapter {

  public void runCheck(String runDirectory) throws IOException {
    PatientExtractor extractor = new PatientExtractor(new SambambaFlagStatParser());
    PatientData patient = extractor.extractFromRunDirectory(runDirectory);

    HealthChecker checker = new MappingHealthChecker();

    checker.isHealthy(patient);
  }
}
