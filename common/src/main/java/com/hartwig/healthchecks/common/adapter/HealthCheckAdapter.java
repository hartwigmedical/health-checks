package com.hartwig.healthchecks.common.adapter;

import org.jetbrains.annotations.NotNull;

public abstract class HealthCheckAdapter {

    public abstract void runCheck(@NotNull final String runDirectory, @NotNull final String reportType);

    public static HealthCheckReportFactory attachReport(String reportType) {
        return () -> {
            return ReportsFlyweight.getInstance().getReport(reportType);
        };
    }
}
