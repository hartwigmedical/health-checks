package com.hartwig.healthchecks.common.adapter;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractHealthCheckAdapter {

    public abstract void runCheck(@NotNull final String runDirectory, @NotNull final String reportType);

    @NotNull
    public static HealthCheckReportFactory attachReport(@NotNull final String reportType) {
        return () -> ReportsFlyweight.getInstance().getReport(reportType);
    }
}
