package com.hartwig.healthchecks.smitty.adapter;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.extractor.DataExtractor;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckCategory;
import com.hartwig.healthchecks.smitty.check.KinshipHealthChecker;
import com.hartwig.healthchecks.smitty.extractor.KinshipExtractor;
import com.hartwig.healthchecks.smitty.reader.KinshipReader;

@ResourceWrapper(type = CheckCategory.SMITTY)
public class SmittyAdapter implements HealthCheckAdapter {

    private final Report report = JsonReport.getInstance();

    @Override
    public void runCheck(@NotNull final String runDirectory) {
        final KinshipReader kinshipReader = new KinshipReader();
        final DataExtractor kinshipExtractor = new KinshipExtractor(kinshipReader);
        final HealthChecker kinshipChecker = new KinshipHealthChecker(runDirectory, kinshipExtractor);
        final BaseReport kinshipReport = kinshipChecker.runCheck();
        report.addReportData(kinshipReport);
    }
}
