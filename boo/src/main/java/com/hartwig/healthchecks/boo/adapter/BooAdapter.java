package com.hartwig.healthchecks.boo.adapter;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.boo.extractor.PrestatsExtractor;
import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.reader.ZipFileReader;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;

@ResourceWrapper(type = CheckCategory.BOO)
public class BooAdapter implements HealthCheckAdapter {

    private final Report report = JsonReport.getInstance();

    @Override
    public void runCheck(@NotNull final String runDirectory) {
        final ZipFileReader zipFileReader = new ZipFileReader();
        final PrestatsExtractor prestatsExtractor = new PrestatsExtractor(zipFileReader);
        final HealthChecker prestastHealthChecker = new HealthCheckerImpl(CheckType.PRESTATS, runDirectory,
                        prestatsExtractor);
        final BaseReport prestats = prestastHealthChecker.runCheck();
        report.addReportData(prestats);

    }
}
