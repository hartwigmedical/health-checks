package com.hartwig.healthchecks.boo.adapter;

import java.io.IOException;

import com.hartwig.healthchecks.boo.extractor.PrestatsExtractor;
import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.exception.MalformedRunDirException;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.path.RunContextFactory;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("WeakerAccess")
@ResourceWrapper(type = CheckCategory.BOO)
public class BooAdapter extends AbstractHealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final RunContext runContext, @NotNull final String reportType) {
        final HealthCheckReportFactory healthCheckReportFactory = AbstractHealthCheckAdapter.attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        // KODU (TODO): Remove this wrapping once all HealthCheckers use the real runContext.
        RunContext realRunContext = null;
        try {
            realRunContext = RunContextFactory.fromRunDirectory(runContext.runDirectory());
        } catch (MalformedRunDirException | IOException e) {
            e.printStackTrace();
        }

        final PrestatsExtractor prestatsExtractor = new PrestatsExtractor(realRunContext);
        final HealthChecker prestatsHealthChecker = new HealthCheckerImpl(CheckType.PRESTATS,
                runContext.runDirectory(), prestatsExtractor);
        final BaseReport prestats = prestatsHealthChecker.runCheck();
        report.addReportData(prestats);
    }
}
