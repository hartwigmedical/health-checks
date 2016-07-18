package com.hartwig.healthchecks.common.checks;

import com.hartwig.healthchecks.common.report.BaseReport;

public interface HealthChecker {

    BaseReport runCheck();
}
