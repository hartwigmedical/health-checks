package com.hartwig.healthchecks.common.io.dir;

import org.jetbrains.annotations.NotNull;

public final class TestRunContextFactory {

    private TestRunContextFactory() {
    }

    @NotNull
    public static RunContext testContext(@NotNull final String runDirectory, @NotNull final String refSample,
            @NotNull final String tumorSample) {
        return new RunContextImpl(runDirectory, refSample, tumorSample);
    }
}
