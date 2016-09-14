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
        final int patientPosition = runDirectory.indexOf("_CPCT") + 1;
        if ((patientPosition == 0) || (runDirectory.length() < (patientPosition + PATIENT_NAME_LENGTH))) {
            throw new MalformedRunDirException(runDirectory);
        }

        final String patient = runDirectory.substring(patientPosition, patientPosition + PATIENT_NAME_LENGTH);

        final String refSample = patient + REF_SAMPLE_SUFFIX;
        final String tumorSample = patient + TUMOR_SAMPLE_SUFFIX;
        final boolean hasPassedTests = runDirectory.length() <= (patientPosition + PATIENT_NAME_LENGTH);

        return new RunContextImpl(runDirectory, refSample, tumorSample, hasPassedTests);
    }
}
