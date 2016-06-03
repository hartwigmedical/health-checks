package com.hartwig.healthchecks.boggs.adapter;

import com.hartwig.healthchecks.boggs.PatientData;
import com.hartwig.healthchecks.boggs.flagstatreader.SambambaFlagStatParser;
import com.hartwig.healthchecks.boggs.healthcheck.HealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.MappingHealthChecker;
import com.hartwig.healthchecks.boggs.io.PatientExtractor;
import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class BoggsAdapter implements HealthCheckAdapter {

    private static Logger LOGGER = LogManager.getLogger(BoggsAdapter.class);

    public boolean runCheck(String runDirectory) {
        try {
            PatientExtractor extractor = new PatientExtractor(new SambambaFlagStatParser());
            PatientData patient = extractor.extractFromRunDirectory(runDirectory);

            HealthChecker checker = new MappingHealthChecker();

            return checker.isHealthy(patient);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }
}
