package com.hartwig.healthchecks.common.io.dir;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.healthchecks.common.exception.MalformedRunDirException;

import org.jetbrains.annotations.NotNull;

public final class CPCTRunContextFactory {

    private static final int PATIENT_NAME_LENGTH = 12;

    @VisibleForTesting
    static final String REF_SAMPLE_SUFFIX = "R";
    @VisibleForTesting
    static final String TUMOR_SAMPLE_SUFFIX = "T";

    private CPCTRunContextFactory() {
    }

    @NotNull
    public static RunContext fromRunDirectory(@NotNull final String runDirectory) throws MalformedRunDirException {
        int patientPosition = runDirectory.indexOf("_CPCT");
        if (patientPosition == -1) {
            throw new MalformedRunDirException(runDirectory);
        }
        String patient = runDirectory.substring(patientPosition + 1);
        if (patient.length() != PATIENT_NAME_LENGTH) {
            throw new MalformedRunDirException(runDirectory);
        }

        String refSample = patient + REF_SAMPLE_SUFFIX;
        String tumorSample = patient + TUMOR_SAMPLE_SUFFIX;

        return new RunContextImpl(runDirectory, refSample, tumorSample);
    }
}
