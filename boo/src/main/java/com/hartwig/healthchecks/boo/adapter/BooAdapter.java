package com.hartwig.healthchecks.boo.adapter;

import com.hartwig.healthchecks.boo.check.PrestatsChecker;
import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.ErrorHandlingChecker;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.result.BaseResult;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ResourceWrapper(type = CheckCategory.BOO)
public class BooAdapter extends AbstractHealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final RunContext runContext, @NotNull final String reportType) {
        final HealthCheckReportFactory healthCheckReportFactory = AbstractHealthCheckAdapter.attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final HealthChecker checker = new PrestatsChecker(runContext);
        final ErrorHandlingChecker prestatsHealthChecker = new ErrorHandlingChecker(checker);
        final BaseResult prestats = prestatsHealthChecker.checkedRun();
        report.addResult(prestats);
    }
}
