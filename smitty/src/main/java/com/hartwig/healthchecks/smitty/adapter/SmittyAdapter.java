package com.hartwig.healthchecks.smitty.adapter;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.reader.Reader;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckCategory;
import com.hartwig.healthchecks.smitty.check.InsertSizeMetricsHealthChecker;
import com.hartwig.healthchecks.smitty.check.KinshipHealthChecker;
import com.hartwig.healthchecks.smitty.extractor.InsertSizeMetricsExtractor;
import com.hartwig.healthchecks.smitty.extractor.KinshipExtractor;
import com.hartwig.healthchecks.smitty.reader.InsertSizeMetricsReader;

import org.jetbrains.annotations.NotNull;

@ResourceWrapper(type = CheckCategory.SMITTY)
public class SmittyAdapter implements HealthCheckAdapter {

    private final Report report = JsonReport.getInstance();

    @Override
    public void runCheck(@NotNull final String runDirectory) {
        final Reader kinshipReader = Reader.build();
        final DataExtractor kinshipExtractor = new KinshipExtractor(kinshipReader);
        final HealthChecker kinshipChecker = new KinshipHealthChecker(runDirectory, kinshipExtractor);
        final BaseReport kinshipReport = kinshipChecker.runCheck();
        report.addReportData(kinshipReport);

        final InsertSizeMetricsReader insertSizeMetricsReader = new InsertSizeMetricsReader();
        final DataExtractor insertSizeExtractor = new InsertSizeMetricsExtractor(insertSizeMetricsReader);
        final HealthChecker insertSizeChecker = new InsertSizeMetricsHealthChecker(runDirectory, insertSizeExtractor);
        final BaseReport insertSizeReport = insertSizeChecker.runCheck();
        report.addReportData(insertSizeReport);
    }
}
