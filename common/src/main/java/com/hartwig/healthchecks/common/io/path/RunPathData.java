package com.hartwig.healthchecks.common.io.path;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.hartwig.healthchecks.common.exception.MalformedRunDirException;

import org.jetbrains.annotations.NotNull;

public class RunPathData implements RunContext {

    // KODU: For example, CPCT12345678
    private static final int PATIENT_NAME_LENGTH = 12;
    private static final String RUN_QC_STATS = "QCStats";
    private static final String RUN_QC_STATS_SAMPLE_SUFFIX = "_dedup";
    private static final String RUN_QC_STATS_INSERT_SIZE_METRICS_EXTENSION = ".insert_size_metrics";

    @NotNull
    private final String runDirectory;
    @NotNull
    private final String refSample;
    @NotNull
    private final String tumorSample;
    @NotNull
    private final Path refSampleInsertSizeMetricsPath;
    @NotNull
    private final Path tumorSampleInsertSizeMetricsPath;

    @NotNull
    public static RunPathData fromRunDirectory(@NotNull final String runDirectory)
            throws MalformedRunDirException, IOException {
        int patientPosition = runDirectory.indexOf("_CPCT");
        if (patientPosition == -1) {
            throw new MalformedRunDirException(runDirectory);
        }
        String patient = runDirectory.substring(patientPosition + 1);
        if (patient.length() != PATIENT_NAME_LENGTH) {
            throw new MalformedRunDirException(runDirectory);
        }

        String refSample = patient + "R";
        String tumorSample = patient + "T";

        String qcPath = runDirectory + File.separator + RUN_QC_STATS + File.separator;
        Path refSampleInsertSizeMetricsPath = SamplePathFinder.build().findPath(
                qcPath + refSample + RUN_QC_STATS_SAMPLE_SUFFIX, refSample,
                RUN_QC_STATS_INSERT_SIZE_METRICS_EXTENSION);
        Path tumorSampleInsertSizeMetricsPath = SamplePathFinder.build().findPath(
                qcPath + tumorSample + RUN_QC_STATS_SAMPLE_SUFFIX, tumorSample,
                RUN_QC_STATS_INSERT_SIZE_METRICS_EXTENSION);

        return new RunPathData(runDirectory, refSample, tumorSample, refSampleInsertSizeMetricsPath,
                tumorSampleInsertSizeMetricsPath);
    }

    RunPathData(@NotNull final String runDirectory, @NotNull final String refSample, @NotNull final String tumorSample,
            @NotNull final Path refSampleInsertSizeMetricsPath, @NotNull final Path tumorSampleInsertSizeMetricsPath) {
        this.runDirectory = runDirectory;
        this.refSample = refSample;
        this.tumorSample = tumorSample;
        this.refSampleInsertSizeMetricsPath = refSampleInsertSizeMetricsPath;
        this.tumorSampleInsertSizeMetricsPath = tumorSampleInsertSizeMetricsPath;
    }

    @NotNull
    @Override
    public String getRunDirectory() {
        return runDirectory;
    }

    @NotNull
    @Override
    public String getRefSample() {
        return refSample;
    }

    @NotNull
    @Override
    public String getTumorSample() {
        return tumorSample;
    }

    @NotNull
    @Override
    public Path getRefSampleInsertSizeMetricsPath() {
        return refSampleInsertSizeMetricsPath;
    }

    @NotNull
    @Override
    public Path getTumorSampleInsertSizeMetricsPath() {
        return tumorSampleInsertSizeMetricsPath;
    }
}

