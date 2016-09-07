package com.hartwig.healthchecks.boggs.adapter;

import com.hartwig.healthchecks.boggs.check.MappingChecker;
import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.ErrorHandlingChecker;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.result.BaseResult;

import org.jetbrains.annotations.NotNull;

public class BoggsAdapter extends AbstractHealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final RunContext runContext, @NotNull final String reportType) {
        final HealthCheckReportFactory healthCheckReportFactory = attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final HealthChecker checker = new MappingChecker(runContext);
        final ErrorHandlingChecker mappingHealthChecker = new ErrorHandlingChecker(checker);
        final BaseResult mapping = mappingHealthChecker.checkedRun();
        report.addResult(mapping);
    }
}
