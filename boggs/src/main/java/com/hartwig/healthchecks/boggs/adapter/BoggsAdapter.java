package com.hartwig.healthchecks.boggs.adapter;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.boggs.flagstatreader.SambambaFlagStatParser;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingExtractor;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsExtractor;
import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsHealthChecker;
import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckCategory;

@ResourceWrapper(type = CheckCategory.BOGGS)
public class BoggsAdapter implements HealthCheckAdapter {

    private final Report report = JsonReport.getInstance();

    @Override
    public void runCheck(@NotNull final String runDirectory) {
        final MappingExtractor mappingExtractor = new MappingExtractor(new SambambaFlagStatParser());
        final HealthChecker mappingHealthChecker = new MappingHealthChecker(runDirectory, mappingExtractor);
        final BaseReport mapping = mappingHealthChecker.runCheck();
        report.addReportData(mapping);

        final PrestatsExtractor prestatsExtractor = new PrestatsExtractor();
        final HealthChecker prestastHealthChecker = new PrestatsHealthChecker(runDirectory, prestatsExtractor);
        final BaseReport prestats = prestastHealthChecker.runCheck();
        report.addReportData(prestats);

    }
}
