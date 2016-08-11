package com.hartwig.healthchecks.common.io.path;

import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;

public interface RunContext {

    @NotNull
    String getRunDirectory();

    @NotNull
    String getRefSample();

    @NotNull
    String getTumorSample();

    @NotNull
    Path getRefSampleInsertSizeMetricsPath();

    @NotNull
    Path getTumorSampleInsertSizeMetricsPath();
}
