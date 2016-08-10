package com.hartwig.healthchecks.common.io.path;

import org.jetbrains.annotations.NotNull;

public final class RunPathDataFactory {

    // KODU: Temporary class while refactoring.

    private RunPathDataFactory() {
    }

    @NotNull
    public RunPathData fromRunDirectory(@NotNull final String runDirectory) {
        return new RunPathData(runDirectory, "bla1", "bla2");
    }
}
