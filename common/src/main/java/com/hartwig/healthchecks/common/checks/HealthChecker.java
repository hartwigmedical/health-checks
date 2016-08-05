package com.hartwig.healthchecks.common.checks;

import com.hartwig.healthchecks.common.report.BaseReport;

import org.jetbrains.annotations.NotNull;

public interface HealthChecker {

    @NotNull
    BaseReport runCheck();
}
