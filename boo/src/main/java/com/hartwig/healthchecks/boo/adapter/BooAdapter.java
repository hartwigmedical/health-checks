package com.hartwig.healthchecks.boo.adapter;

import com.hartwig.healthchecks.boo.extractor.PrestatsExtractor;
import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.reader.ZipFilesReader;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;

import org.jetbrains.annotations.NotNull;

@ResourceWrapper(type = CheckCategory.BOO)
public class BooAdapter extends AbstractHealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final String runDirectory, @NotNull final String reportType) {

        final HealthCheckReportFactory healthCheckReportFactory = AbstractHealthCheckAdapter.attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final ZipFilesReader zipFileReader = new ZipFilesReader();
        final PrestatsExtractor prestatsExtractor = new PrestatsExtractor(zipFileReader);
        final HealthChecker prestastHealthChecker = new HealthCheckerImpl(CheckType.PRESTATS, runDirectory,
                        prestatsExtractor);
        final BaseReport prestats = prestastHealthChecker.runCheck();
        report.addReportData(prestats);

    }
}
