package com.hartwig.healthchecks.common.io.path;

import org.jetbrains.annotations.NotNull;

public class RunDirPathResolver {

    @NotNull
    private final String runDirectory;
    @NotNull
    private final String refSample;
    @NotNull
    private final String tumorSample;

    public RunDirPathResolver(@NotNull final String runDirectory, @NotNull final String refSample,
            @NotNull final String tumorSample) {
        this.runDirectory = runDirectory;
        this.refSample = refSample;
        this.tumorSample = tumorSample;
    }

}

