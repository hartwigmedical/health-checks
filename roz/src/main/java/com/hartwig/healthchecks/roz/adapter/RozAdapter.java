package com.hartwig.healthchecks.roz.adapter;

import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.ErrorHandlingChecker;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.roz.check.SlicedChecker;

import org.jetbrains.annotations.NotNull;

public class RozAdapter extends AbstractHealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final RunContext runContext, @NotNull final String reportType) {
        final HealthCheckReportFactory healthCheckReportFactory = AbstractHealthCheckAdapter.attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final HealthChecker checker = new SlicedChecker(runContext);
        final ErrorHandlingChecker healthCheck = new ErrorHandlingChecker(checker);
        final BaseResult baseResult = healthCheck.checkedRun();
        report.addResult(baseResult);
    }
}
