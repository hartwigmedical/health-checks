package com.hartwig.healthchecks.roz.adapter;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.reader.ExtensionLineReader;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.roz.extractor.SlicedExtractor;

@ResourceWrapper(type = CheckCategory.ROZ)
public class RozAdapter implements HealthCheckAdapter {

    private final Report report = JsonReport.getInstance();

    @Override
    public void runCheck(@NotNull final String runDirectory) {
        final ExtensionLineReader reader = ExtensionLineReader.build();
        final DataExtractor extractor = new SlicedExtractor(reader);
        final HealthChecker healthcheck = new HealthCheckerImpl(CheckType.SLICED, runDirectory, extractor);
        final BaseReport baseReport = healthcheck.runCheck();
        report.addReportData(baseReport);
    }
}
