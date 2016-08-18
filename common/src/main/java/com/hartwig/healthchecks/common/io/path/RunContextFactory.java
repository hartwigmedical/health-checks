package com.hartwig.healthchecks.common.io.path;

import java.io.File;
import java.io.IOException;

import com.hartwig.healthchecks.common.exception.MalformedRunDirException;

import org.jetbrains.annotations.NotNull;

public final class RunContextFactory {

    private static final int PATIENT_NAME_LENGTH = 12;

    private static final String REF_SAMPLE_SUFFIX = "R";
    private static final String TUMOR_SAMPLE_SUFFIX = "T";

    private static final String RUN_LOG_DIR = "logs";
    private static final String RUN_SOMATICS_DIR = "Somatics";
    private static final String RUN_SOMATICS_DIR_SAMPLE_CONNECTOR = "_";

    private static final String SAMPLE_MAPPING_DIR = "mapping";
    private static final String SAMPLE_QCSTATS_DIR = "QCStats";
    private static final String RUN_QCSTATS_DIR = "QCStats";
    private static final String RUN_QCSTATS_DIR_SUFFIX = "_dedup";

    private RunContextFactory() {
    }

    @NotNull
    public static RunContext backwardsCompatible(@NotNull final String runDirectory) {
        return new RunContextImpl(runDirectory, null, null);
    }

    @NotNull
    // TODO (KODU): Belongs in test-package
    public static RunContext testContext(@NotNull final String runDirectory, @NotNull final String refSample,
            @NotNull final String tumorSample) {
        return new RunContextImpl(runDirectory, refSample, tumorSample);
    }

    @NotNull
    // TODO (KODU): Belongs in test-package
    public static SampleContext testSampleContext(@NotNull final String sampleId, @NotNull final String mapping,
            @NotNull final String sampleQcStats, @NotNull final String runQcStats) {
        return new SampleContextImpl(sampleId, mapping, sampleQcStats, runQcStats);
    }

    @NotNull
    public static RunContext fromRunDirectory(@NotNull final String runDirectory)
            throws MalformedRunDirException, IOException {
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

        //        SampleContext refSampleContext = sampleContextFromRunDirectory(runDirectory, refSampleId);
        //        SampleContext tumorSampleContext = sampleContextFromRunDirectory(runDirectory, tumorSampleId);

        //        String logsDirectory = runDirectory + File.separator + RUN_LOG_DIR;
        //        String somaticsDirectory = runDirectory + File.separator + RUN_SOMATICS_DIR + File.separator + refSampleId
        //                + RUN_SOMATICS_DIR_SAMPLE_CONNECTOR + tumorSampleId;

        return new RunContextImpl(runDirectory, refSample, tumorSample);
    }

    @NotNull
    private static SampleContext sampleContextFromRunDirectory(@NotNull final String runDirectory,
            @NotNull final String sampleId) {
        String sampleDirectory = runDirectory + File.separator + sampleId;

        String mappingDirectory = sampleDirectory + File.separator + SAMPLE_MAPPING_DIR;
        String sampleQcDirectory = sampleDirectory + File.separator + SAMPLE_QCSTATS_DIR;
        String runQcDirectory =
                runDirectory + File.separator + RUN_QCSTATS_DIR + File.separator + sampleId + RUN_QCSTATS_DIR_SUFFIX;
        return new SampleContextImpl(sampleId, mappingDirectory, sampleQcDirectory, runQcDirectory);
    }
}
