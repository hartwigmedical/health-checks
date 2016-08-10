package com.hartwig.healthchecks.common.adapter;

import com.hartwig.healthchecks.common.io.path.RunPathData;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractHealthCheckAdapter {

    public abstract void runCheck(@NotNull final RunPathData runPathData, @NotNull final String reportType);

    @NotNull
    public static HealthCheckReportFactory attachReport(@NotNull final String reportType) {
        return () -> ReportsFlyweight.getInstance().getReport(reportType);
    }
}
