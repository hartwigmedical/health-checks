package com.hartwig.healthchecks.common.checks;

import java.io.IOException;

public interface HealthChecker {

	boolean isHealthy() throws IOException;
}
