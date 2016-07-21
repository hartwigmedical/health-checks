package com.hartwig.healthchecks.nesbit.adapter;

import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.reader.ExtensionLineReader;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.nesbit.extractor.GermlineExtractor;
import com.hartwig.healthchecks.nesbit.extractor.SomaticExtractor;

import org.jetbrains.annotations.NotNull;

@ResourceWrapper(type = CheckCategory.NESBIT)
public class NesbitAdapter extends AbstractHealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final String runDirectory, @NotNull final String reportType) {

        final HealthCheckReportFactory healthCheckReportFactory = AbstractHealthCheckAdapter.attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final ExtensionLineReader germlineReader = ExtensionLineReader.build();
        final DataExtractor germlineExtractor = new GermlineExtractor(germlineReader);
        final HealthChecker germline = new HealthCheckerImpl(CheckType.GERMLINE, runDirectory, germlineExtractor);
        final BaseReport germlineReport = germline.runCheck();
        report.addReportData(germlineReport);

        final ExtensionLineReader somaticReader = ExtensionLineReader.build();
        final DataExtractor somaticExtractor = new SomaticExtractor(somaticReader);
        final HealthChecker somatic = new HealthCheckerImpl(CheckType.SOMATIC, runDirectory, somaticExtractor);
        final BaseReport somaticReport = somatic.runCheck();
        report.addReportData(somaticReport);
    }
}
