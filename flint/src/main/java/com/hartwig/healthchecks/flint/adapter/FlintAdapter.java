package com.hartwig.healthchecks.flint.adapter;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.util.CheckCategory;

import org.jetbrains.annotations.NotNull;

@ResourceWrapper(type = CheckCategory.FLINT)
public class FlintAdapter implements HealthCheckAdapter {

    private final Report report = JsonReport.getInstance();

    @Override
    public void runCheck(@NotNull final String runDirectory) {
    }
}
