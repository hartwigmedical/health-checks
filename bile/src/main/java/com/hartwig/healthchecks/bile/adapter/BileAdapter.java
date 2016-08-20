package com.hartwig.healthchecks.bile.adapter;

import com.hartwig.healthchecks.bile.extractor.RealignerExtractor;
import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ResourceWrapper(type = CheckCategory.BILE)
public class BileAdapter extends AbstractHealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final RunContext runContext, @NotNull final String reportType) {
        final HealthCheckReportFactory healthCheckReportFactory = AbstractHealthCheckAdapter.attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final DataExtractor extractor = new RealignerExtractor(runContext);

        final HealthChecker healthCheck = new HealthCheckerImpl(CheckType.REALIGNER, runContext.runDirectory(),
                extractor);
        final BaseReport baseReport = healthCheck.runCheck();
        report.addReportData(baseReport);
    }
}
