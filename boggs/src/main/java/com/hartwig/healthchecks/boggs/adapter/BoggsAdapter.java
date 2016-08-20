package com.hartwig.healthchecks.boggs.adapter;

import com.hartwig.healthchecks.boggs.extractor.MappingExtractor;
import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;

import org.jetbrains.annotations.NotNull;

@ResourceWrapper(type = CheckCategory.BOGGS)
public class BoggsAdapter extends AbstractHealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final RunContext runContext, @NotNull final String reportType) {
        final HealthCheckReportFactory healthCheckReportFactory = attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final MappingExtractor mappingExtractor = new MappingExtractor(runContext);
        final HealthChecker mappingHealthChecker = new HealthCheckerImpl(CheckType.MAPPING, runContext.runDirectory(),
                mappingExtractor);
        final BaseReport mapping = mappingHealthChecker.runCheck();
        report.addReportData(mapping);
    }
}
