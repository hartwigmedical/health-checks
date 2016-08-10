package com.hartwig.healthchecks.boggs.adapter;

import com.hartwig.healthchecks.boggs.extractor.MappingExtractor;
import com.hartwig.healthchecks.boggs.flagstatreader.SambambaFlagStatParser;
import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.path.RunPathData;
import com.hartwig.healthchecks.common.io.path.SamplePathFinder;
import com.hartwig.healthchecks.common.io.reader.ZipFilesReader;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;

import org.jetbrains.annotations.NotNull;

@ResourceWrapper(type = CheckCategory.BOGGS)
public class BoggsAdapter extends AbstractHealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final RunPathData runPathData, @NotNull final String reportType) {
        final HealthCheckReportFactory healthCheckReportFactory = attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final ZipFilesReader zipFileReader = new ZipFilesReader();
        final SamplePathFinder samplePathFinder = SamplePathFinder.build();
        final SambambaFlagStatParser flagstatParser = new SambambaFlagStatParser();

        final MappingExtractor mappingExtractor = new MappingExtractor(flagstatParser, zipFileReader,
                samplePathFinder);
        final HealthChecker mappingHealthChecker = new HealthCheckerImpl(CheckType.MAPPING,
                runPathData.getRunDirectory(), mappingExtractor);
        final BaseReport mapping = mappingHealthChecker.runCheck();
        report.addReportData(mapping);
    }
}
