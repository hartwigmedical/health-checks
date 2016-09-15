package com.hartwig.healthchecks.common.io.dir;

import java.io.File;

import com.hartwig.healthchecks.common.exception.MalformedRunDirException;

import org.jetbrains.annotations.NotNull;

public final class CPCTRunContextFactory {

    private static final int CPCT_PATIENT_NAME_LENGTH = 12;

    private static final String REF_SAMPLE_SUFFIX = "R";
    private static final String TUMOR_SAMPLE_SUFFIX = "T";

    private CPCTRunContextFactory() {
    }

    @NotNull
    public static RunContext fromRunDirectory(@NotNull final String runDirectory) throws MalformedRunDirException {
        final int patientPosition = runDirectory.indexOf("_CPCT") + 1;
        if ((patientPosition == 0) || (runDirectory.length() < (patientPosition + CPCT_PATIENT_NAME_LENGTH))) {
            throw new MalformedRunDirException(runDirectory);
        }

        final String patient = runDirectory.substring(patientPosition, patientPosition + CPCT_PATIENT_NAME_LENGTH);

        final boolean hasPassedTests = runDirectory.length() <= (patientPosition + CPCT_PATIENT_NAME_LENGTH);

        final File[] runContents = new File(runDirectory).listFiles();
        assert runContents != null;

        String refSample = null;
        String tumorSample = null;
        for (final File content : runContents) {
            if (content.isDirectory() && content.getName().contains(patient)) {
                if (content.getName().contains(REF_SAMPLE_SUFFIX)) {
                    refSample = content.getName();
                } else if (content.getName().contains(TUMOR_SAMPLE_SUFFIX)) {
                    tumorSample = content.getName();
                }
            }
        }

        if (refSample == null || tumorSample == null) {
            throw new MalformedRunDirException(runDirectory);
        }

        return new RunContextImpl(runDirectory, refSample, tumorSample, hasPassedTests);
    }
}
