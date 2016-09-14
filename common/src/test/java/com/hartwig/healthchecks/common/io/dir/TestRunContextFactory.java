package com.hartwig.healthchecks.common.io.dir;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestRunContextFactory {

    private TestRunContextFactory() {
    }

    @NotNull
    public static RunContext forTest(@NotNull final String runDirectory) {
        return forTest(runDirectory, Strings.EMPTY, Strings.EMPTY);
    }

    @NotNull
    public static RunContext forTest(@NotNull final String runDirectory, @NotNull final String refSample,
            @NotNull final String tumorSample) {
        return new RunContextImpl(runDirectory, refSample, tumorSample, false);
    }
}
