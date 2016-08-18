package com.hartwig.healthchecks.flint.adapter;

import java.io.IOException;

import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.exception.MalformedRunDirException;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.path.RunContextFactory;
import com.hartwig.healthchecks.common.report.BaseReport;
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

        // KODU (TODO): Remove this wrapping once all HealthCheckers use the real runContext.
        RunContext realRunContext = null;
        try {
            realRunContext = RunContextFactory.fromRunDirectory(runContext.runDirectory());
        } catch (MalformedRunDirException | IOException e) {
            e.printStackTrace();
        }

        final DataExtractor insertSizeExtractor = new InsertSizeMetricsExtractor(realRunContext);
        final HealthChecker insertSizeChecker = new HealthCheckerImpl(CheckType.INSERT_SIZE, runContext.runDirectory(),
                insertSizeExtractor);
        final BaseReport insertSizeReport = insertSizeChecker.runCheck();
        report.addReportData(insertSizeReport);

        final DataExtractor summaryExtractor = new SummaryMetricsExtractor(realRunContext);
        final HealthChecker summaryChecker = new HealthCheckerImpl(CheckType.SUMMARY_METRICS,
                runContext.runDirectory(), summaryExtractor);
        final BaseReport summaryReport = summaryChecker.runCheck();
        report.addReportData(summaryReport);

        final DataExtractor wgsExtractor = new WGSMetricsExtractor(realRunContext);
        final HealthChecker coverageChecker = new HealthCheckerImpl(CheckType.COVERAGE, runContext.runDirectory(),
                wgsExtractor);
        final BaseReport coverageReport = coverageChecker.runCheck();
        report.addReportData(coverageReport);
    }
}
