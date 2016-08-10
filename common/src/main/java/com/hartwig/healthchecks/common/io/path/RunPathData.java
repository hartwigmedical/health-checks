package com.hartwig.healthchecks.common.io.path;

import org.jetbrains.annotations.NotNull;

public class RunPathData {

    @NotNull
    private final String runDirectory;
    @NotNull
    private final String refSample;
    @NotNull
    private final String tumorSample;

    @NotNull
    public static RunPathData fromRunDirectory(@NotNull final String runDirectory) {
        String patient = runDirectory.substring(runDirectory.indexOf("CPCT"));
        return new RunPathData(runDirectory, patient + "R", patient + "T");
    }

    RunPathData(@NotNull final String runDirectory, @NotNull final String refSample,
            @NotNull final String tumorSample) {
        this.runDirectory = runDirectory;
        this.refSample = refSample;
        this.tumorSample = tumorSample;
    }

    @NotNull
    public String getRunDirectory() {
        return runDirectory;
    }
}

