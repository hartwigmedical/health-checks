package com.hartwig.healthchecks.common.checks;

import com.hartwig.healthchecks.common.util.BaseReport;

public interface HealthChecker {

    BaseReport runCheck();
}
