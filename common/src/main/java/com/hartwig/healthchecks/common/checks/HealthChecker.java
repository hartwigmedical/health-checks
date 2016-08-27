package com.hartwig.healthchecks.common.checks;

import com.hartwig.healthchecks.common.data.BaseReport;

import org.jetbrains.annotations.NotNull;

public interface HealthChecker {

    @NotNull
    BaseReport runCheck();
}
