package com.hartwig.healthchecks.boggs.adapter;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.boggs.flagstatreader.SambambaFlagStatParser;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingExtractor;
import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsExtractor;
import com.hartwig.healthchecks.boggs.reader.ZipFileReader;
import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckCategory;
import com.hartwig.healthchecks.common.util.CheckType;

@ResourceWrapper(type = CheckCategory.BOGGS)
public class BoggsAdapter implements HealthCheckAdapter {

    private final Report report = JsonReport.getInstance();

    @Override
    public void runCheck(@NotNull final String runDirectory) {
        final ZipFileReader zipFileReader = new ZipFileReader();
        final MappingExtractor mappingExtractor = new MappingExtractor(new SambambaFlagStatParser(), zipFileReader);
        final HealthChecker mappingHealthChecker = new HealthCheckerImpl(CheckType.MAPPING, runDirectory,
                        mappingExtractor);
        final BaseReport mapping = mappingHealthChecker.runCheck();
        report.addReportData(mapping);

        final PrestatsExtractor prestatsExtractor = new PrestatsExtractor(zipFileReader);
        final HealthChecker prestastHealthChecker = new HealthCheckerImpl(CheckType.PRESTATS, runDirectory,
                        prestatsExtractor);
        final BaseReport prestats = prestastHealthChecker.runCheck();
        report.addReportData(prestats);

    }
}
