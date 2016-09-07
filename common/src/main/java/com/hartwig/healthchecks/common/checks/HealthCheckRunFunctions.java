package com.hartwig.healthchecks.common.checks;

import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.report.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.report.ReportsFlyweight;

import org.jetbrains.annotations.NotNull;

public final class HealthCheckRunFunctions {

    private HealthCheckRunFunctions() {
    }

    public static void runCheck(@NotNull final RunContext runContext, @NotNull final String reportType,
            @NotNull final HealthChecker checker) {
        final HealthCheckReportFactory healthCheckReportFactory = attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final ErrorHandlingChecker errorHandlingChecker = new ErrorHandlingChecker(checker);
        report.addResult(errorHandlingChecker.checkedRun());
    }

    @NotNull
    public static HealthCheckReportFactory attachReport(@NotNull final String reportType) {
        return () -> ReportsFlyweight.getInstance().getReport(reportType);
    }
}
