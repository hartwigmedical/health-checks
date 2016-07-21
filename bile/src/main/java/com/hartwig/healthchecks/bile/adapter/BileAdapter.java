package com.hartwig.healthchecks.bile.adapter;

import com.hartwig.healthchecks.bile.extractor.RealignerExtractor;
import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.path.SamplePathFinder;
import com.hartwig.healthchecks.common.io.reader.SampleReader;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;

import org.jetbrains.annotations.NotNull;

@ResourceWrapper(type = CheckCategory.BILE)
public class BileAdapter extends HealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final String runDirectory, @NotNull final String reportType) {

        final HealthCheckReportFactory healthCheckReportFactory = HealthCheckAdapter.attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final SampleReader reader = SampleReader.build();
        final SamplePathFinder samplePathFinder = SamplePathFinder.build();
        final DataExtractor extractor = new RealignerExtractor(reader, samplePathFinder);

        final HealthChecker healthcheck = new HealthCheckerImpl(CheckType.REALIGNER, runDirectory, extractor);
        final BaseReport baseReport = healthcheck.runCheck();
        report.addReportData(baseReport);
    }
}
