package com.hartwig.healthchecks.flint.adapter;

import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.reader.SampleReader;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.flint.extractor.InsertSizeMetricsExtractor;
import com.hartwig.healthchecks.flint.extractor.SummaryMetricsExtractor;
import com.hartwig.healthchecks.flint.extractor.WGSExtractor;

import org.jetbrains.annotations.NotNull;

@ResourceWrapper(type = CheckCategory.FLINT)
public class FlintAdapter extends AbstractHealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final String runDirectory, @NotNull final String reportType) {

        final HealthCheckReportFactory healthCheckReportFactory = AbstractHealthCheckAdapter.attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final SampleReader insertSizeMetricsReader = SampleReader.build();
        final DataExtractor insertSizeExtractor = new InsertSizeMetricsExtractor(insertSizeMetricsReader);
        final HealthChecker insertSizeChecker = new HealthCheckerImpl(CheckType.INSERT_SIZE, runDirectory,
                        insertSizeExtractor);
        final BaseReport insertSizeReport = insertSizeChecker.runCheck();
        report.addReportData(insertSizeReport);

        final SampleReader summaryReader = SampleReader.build();
        final DataExtractor summaryExtractor = new SummaryMetricsExtractor(summaryReader);
        final HealthChecker summaryChecker = new HealthCheckerImpl(CheckType.SUMMARY_METRICS, runDirectory,
                        summaryExtractor);
        final BaseReport summaryReport = summaryChecker.runCheck();
        report.addReportData(summaryReport);

        final SampleReader wgsReader = SampleReader.build();
        final DataExtractor wgsExtractor = new WGSExtractor(wgsReader);
        final HealthChecker coverageChecker = new HealthCheckerImpl(CheckType.COVERAGE, runDirectory, wgsExtractor);
        final BaseReport coverageReport = coverageChecker.runCheck();
        report.addReportData(coverageReport);
    }
}
