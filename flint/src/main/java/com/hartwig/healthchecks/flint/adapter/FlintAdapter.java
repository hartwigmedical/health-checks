package com.hartwig.healthchecks.flint.adapter;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.extractor.DataExtractor;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckCategory;
import com.hartwig.healthchecks.flint.check.InsertSizeMetricsHealthChecker;
import com.hartwig.healthchecks.flint.extractor.InsertSizeMetricsExtractor;
import com.hartwig.healthchecks.flint.reader.InsertSizeMetricsReader;

@ResourceWrapper(type = CheckCategory.FLINT)
public class FlintAdapter implements HealthCheckAdapter {

    private final Report report = JsonReport.getInstance();

    @Override
    public void runCheck(@NotNull final String runDirectory) {
        final InsertSizeMetricsReader insertSizeMetricsReader = new InsertSizeMetricsReader();
        final DataExtractor insertSizeExtractor = new InsertSizeMetricsExtractor(insertSizeMetricsReader);
        final HealthChecker insertSizeChecker = new InsertSizeMetricsHealthChecker(runDirectory, insertSizeExtractor);
        final BaseReport insertSizeReport = insertSizeChecker.runCheck();
        report.addReportData(insertSizeReport);
    }
}
