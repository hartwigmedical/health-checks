package com.hartwig.healthchecks.common.io.path;

import org.jetbrains.annotations.NotNull;

public interface RunContext {

    @NotNull
    String runDirectory();

    @NotNull
    SampleContext refSample();

    @NotNull
    SampleContext tumorSample();

    @NotNull
    String logs();

    @NotNull
    String somatics();
}
