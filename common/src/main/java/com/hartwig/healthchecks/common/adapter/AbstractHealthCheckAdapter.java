package com.hartwig.healthchecks.common.adapter;

import com.hartwig.healthchecks.common.io.path.RunContext;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractHealthCheckAdapter {

    public abstract void runCheck(@NotNull final RunContext runContext, @NotNull final String reportType);

    @NotNull
    public static HealthCheckReportFactory attachReport(@NotNull final String reportType) {
        return () -> ReportsFlyweight.getInstance().getReport(reportType);
    }
}
