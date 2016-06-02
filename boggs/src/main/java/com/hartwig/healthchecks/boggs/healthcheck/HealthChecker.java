package com.hartwig.healthchecks.boggs.healthcheck;

import com.hartwig.healthchecks.boggs.PatientData;
import org.jetbrains.annotations.NotNull;

public interface HealthChecker {

    boolean isHealthy(@NotNull PatientData patient);
}
