package com.hartwig.healthchecks.flint.adapter;

import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.path.RunPathData;
import com.hartwig.healthchecks.common.io.reader.SampleFinderAndReader;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.flint.extractor.InsertSizeMetricsExtractor;
import com.hartwig.healthchecks.flint.extractor.SummaryMetricsExtractor;
import com.hartwig.healthchecks.flint.extractor.WGSMetricsExtractor;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("WeakerAccess")
@ResourceWrapper(type = CheckCategory.FLINT)
public class FlintAdapter extends AbstractHealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final RunPathData runPathData, @NotNull final String reportType) {
        final HealthCheckReportFactory healthCheckReportFactory = AbstractHealthCheckAdapter.attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final SampleFinderAndReader insertSizeMetricsReader = SampleFinderAndReader.build();
        final DataExtractor insertSizeExtractor = new InsertSizeMetricsExtractor(insertSizeMetricsReader);
        final HealthChecker insertSizeChecker = new HealthCheckerImpl(CheckType.INSERT_SIZE,
                runPathData.getRunDirectory(), insertSizeExtractor);
        final BaseReport insertSizeReport = insertSizeChecker.runCheck();
        report.addReportData(insertSizeReport);

        final SampleFinderAndReader summaryReader = SampleFinderAndReader.build();
        final DataExtractor summaryExtractor = new SummaryMetricsExtractor(summaryReader);
        final HealthChecker summaryChecker = new HealthCheckerImpl(CheckType.SUMMARY_METRICS,
                runPathData.getRunDirectory(), summaryExtractor);
        final BaseReport summaryReport = summaryChecker.runCheck();
        report.addReportData(summaryReport);

        final SampleFinderAndReader wgsReader = SampleFinderAndReader.build();
        final DataExtractor wgsExtractor = new WGSMetricsExtractor(wgsReader);
        final HealthChecker coverageChecker = new HealthCheckerImpl(CheckType.COVERAGE, runPathData.getRunDirectory(),
                wgsExtractor);
        final BaseReport coverageReport = coverageChecker.runCheck();
        report.addReportData(coverageReport);
    }
}
