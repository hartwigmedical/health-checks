package com.hartwig.healthchecks.bile.adapter;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.bile.extractor.RealignerExtractor;
import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.path.SamplePathFinder;
import com.hartwig.healthchecks.common.io.reader.SampleReader;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckCategory;
import com.hartwig.healthchecks.common.util.CheckType;

@ResourceWrapper(type = CheckCategory.BILE)
public class BileAdapter implements HealthCheckAdapter {

    private final Report report = JsonReport.getInstance();

    @Override
    public void runCheck(@NotNull final String runDirectory) {
        final SampleReader reader = SampleReader.build();
        final SamplePathFinder samplePathFinder = SamplePathFinder.build();
        final DataExtractor extractor = new RealignerExtractor(reader, samplePathFinder);

        final HealthChecker healthcheck = new HealthCheckerImpl(CheckType.REALIGNER, runDirectory, extractor);
        final BaseReport baseReport = healthcheck.runCheck();
        report.addReportData(baseReport);
    }
}