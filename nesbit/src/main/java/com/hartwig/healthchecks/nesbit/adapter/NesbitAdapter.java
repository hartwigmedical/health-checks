package com.hartwig.healthchecks.nesbit.adapter;

import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.ErrorHandlingChecker;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.nesbit.check.GermlineChecker;
import com.hartwig.healthchecks.nesbit.check.SomaticChecker;

import org.jetbrains.annotations.NotNull;

public class NesbitAdapter extends AbstractHealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final RunContext runContext, @NotNull final String reportType) {
        final HealthCheckReportFactory healthCheckReportFactory = AbstractHealthCheckAdapter.attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final HealthChecker germlineChecker = new GermlineChecker(runContext);
        final ErrorHandlingChecker germline = new ErrorHandlingChecker(germlineChecker);
        final BaseResult germlineResult = germline.checkedRun();
        report.addResult(germlineResult);

        final HealthChecker somaticChecker = new SomaticChecker(runContext);
        final ErrorHandlingChecker somatic = new ErrorHandlingChecker(somaticChecker);
        final BaseResult somaticResult = somatic.checkedRun();
        report.addResult(somaticResult);
    }
}
