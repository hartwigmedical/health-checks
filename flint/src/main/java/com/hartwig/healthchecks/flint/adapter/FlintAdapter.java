package com.hartwig.healthchecks.flint.adapter;

import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.data.BaseResult;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.flint.extractor.InsertSizeMetricsExtractor;
import com.hartwig.healthchecks.flint.extractor.SummaryMetricsExtractor;
import com.hartwig.healthchecks.flint.extractor.WGSMetricsExtractor;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ResourceWrapper(type = CheckCategory.FLINT)
public class FlintAdapter extends AbstractHealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final RunContext runContext, @NotNull final String reportType) {
        final HealthCheckReportFactory healthCheckReportFactory = AbstractHealthCheckAdapter.attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final DataExtractor insertSizeExtractor = new InsertSizeMetricsExtractor(runContext);
        final HealthChecker insertSizeChecker = new HealthCheckerImpl(CheckType.INSERT_SIZE, insertSizeExtractor);
        final BaseResult insertSizeReport = insertSizeChecker.runCheck();
        report.addReportData(insertSizeReport);

        final DataExtractor summaryExtractor = new SummaryMetricsExtractor(runContext);
        final HealthChecker summaryChecker = new HealthCheckerImpl(CheckType.SUMMARY_METRICS, summaryExtractor);
        final BaseResult summaryReport = summaryChecker.runCheck();
        report.addReportData(summaryReport);

        final DataExtractor wgsExtractor = new WGSMetricsExtractor(runContext);
        final HealthChecker coverageChecker = new HealthCheckerImpl(CheckType.COVERAGE, wgsExtractor);
        final BaseResult coverageReport = coverageChecker.runCheck();
        report.addReportData(coverageReport);
    }
}
