package com.hartwig.healthchecks.nesbit.adapter;

import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.data.BaseResult;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.nesbit.extractor.GermlineExtractor;
import com.hartwig.healthchecks.nesbit.extractor.SomaticExtractor;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ResourceWrapper(type = CheckCategory.NESBIT)
public class NesbitAdapter extends AbstractHealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final RunContext runContext, @NotNull final String reportType) {
        final HealthCheckReportFactory healthCheckReportFactory = AbstractHealthCheckAdapter.attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final DataExtractor germlineExtractor = new GermlineExtractor(runContext);
        final HealthChecker germline = new HealthCheckerImpl(CheckType.GERMLINE, germlineExtractor);
        final BaseResult germlineReport = germline.runCheck();
        report.addReportData(germlineReport);

        final DataExtractor somaticExtractor = new SomaticExtractor(runContext);
        final HealthChecker somatic = new HealthCheckerImpl(CheckType.SOMATIC, somaticExtractor);
        final BaseResult somaticReport = somatic.runCheck();
        report.addReportData(somaticReport);
    }
}
