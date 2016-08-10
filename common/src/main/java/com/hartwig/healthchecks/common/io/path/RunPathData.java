package com.hartwig.healthchecks.common.io.path;

import com.hartwig.healthchecks.common.exception.MalformedRunDirException;

import org.jetbrains.annotations.NotNull;

public class RunPathData {

    private static final int PATIENT_NAME_LENGTH = 12;

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
        if (patient.length() != PATIENT_NAME_LENGTH) {
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
    String getRefSample() {
        return refSample;
    }

    @NotNull
    String getTumorSample() {
        return tumorSample;
    }
}

