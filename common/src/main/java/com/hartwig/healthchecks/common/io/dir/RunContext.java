package com.hartwig.healthchecks.common.io.dir;

import org.jetbrains.annotations.NotNull;

public interface RunContext {

    @NotNull
    String runDirectory();

    @NotNull
    String refSample();

    @NotNull
    String tumorSample();

    boolean hasPassedTests();
}
