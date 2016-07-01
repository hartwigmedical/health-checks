package com.hartwig.healthchecks.flint.adapter;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.util.CheckCategory;

import org.jetbrains.annotations.NotNull;

@ResourceWrapper(type = CheckCategory.FLINT)
public class FlintAdapter implements HealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final String runDirectory) {
        // Work in progress.
    }
}
