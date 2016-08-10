package com.hartwig.healthchecks.smitty.adapter;

import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.path.RunPathData;
import com.hartwig.healthchecks.common.io.reader.FileFinderAndReader;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.smitty.extractor.KinshipExtractor;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("WeakerAccess")
@ResourceWrapper(type = CheckCategory.SMITTY)
public class SmittyAdapter extends AbstractHealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final RunPathData runPathData, @NotNull final String reportType) {
        final HealthCheckReportFactory healthCheckReportFactory = AbstractHealthCheckAdapter.attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final FileFinderAndReader kinshipReader = FileFinderAndReader.build();
        final DataExtractor kinshipExtractor = new KinshipExtractor(kinshipReader);
        final HealthChecker kinshipChecker = new HealthCheckerImpl(CheckType.KINSHIP, runPathData.getRunDirectory(),
                kinshipExtractor);
        final BaseReport kinshipReport = kinshipChecker.runCheck();
        report.addReportData(kinshipReport);
    }
}
