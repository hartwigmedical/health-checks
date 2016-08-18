package com.hartwig.healthchecks.common.io.path;

import org.jetbrains.annotations.NotNull;

public interface RunContext {

    @NotNull
    String runDirectory();

    @NotNull
    String refSample();

    @NotNull
    String tumorSample();
}
