package com.hartwig.healthchecks.common.checks;

import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.report.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.report.Report;

import org.jetbrains.annotations.NotNull;

public final class HealthCheckRunFunctions {

    private HealthCheckRunFunctions() {
    }

    public static void runCheck(@NotNull final RunContext runContext, @NotNull final String reportType,
            @NotNull final HealthChecker checker) {
        final Report report = HealthCheckReportFactory.create(reportType);
        final ErrorHandlingChecker errorHandlingChecker = new ErrorHandlingChecker(checker);
        report.addResult(errorHandlingChecker.checkedRun(runContext));
    }
}
