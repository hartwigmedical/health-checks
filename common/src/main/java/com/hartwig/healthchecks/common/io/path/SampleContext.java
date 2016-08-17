package com.hartwig.healthchecks.common.io.path;

import org.jetbrains.annotations.NotNull;

public interface SampleContext {

    @NotNull
    String sampleId();

    @NotNull
    String mapping();

    @NotNull
    String sampleQcStats();

    @NotNull
    String runQcStats();
}
