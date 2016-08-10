package com.hartwig.healthchecks.common.io.path;

import com.hartwig.healthchecks.common.exception.MalformedRunDirException;

import org.jetbrains.annotations.NotNull;

public class RunPathData {

    @NotNull
    private final String runDirectory;
    @NotNull
    private final String refSample;
    @NotNull
    private final String tumorSample;

    @NotNull
    public static RunPathData fromRunDirectory(@NotNull final String runDirectory) throws MalformedRunDirException {
        int patientPosition = runDirectory.indexOf("_CPCT");
        if (patientPosition == -1) {
            throw new MalformedRunDirException(runDirectory);
        }
        String patient = runDirectory.substring(patientPosition + 1);
        if (patient.length() != 12) {
            throw new MalformedRunDirException(runDirectory);
        }
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

    @NotNull
    public String getRefSample() {
        return refSample;
    }

    @NotNull
    public String getTumorSample() {
        return tumorSample;
    }
}

