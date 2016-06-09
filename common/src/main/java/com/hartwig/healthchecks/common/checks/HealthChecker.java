package com.hartwig.healthchecks.common.checks;

import java.io.IOException;

import com.hartwig.healthchecks.common.util.BaseReport;

public interface HealthChecker {
	
	BaseReport runCheck() throws IOException;
}
