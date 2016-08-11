package com.hartwig.healthchecks.common.io.path;

import org.jetbrains.annotations.NotNull;

public final class RunContextTempFactory {

    // KODU: Temporary class while refactoring.

    private RunContextTempFactory() {
    }

    @NotNull
    public static RunContext fromRunDirectory(@NotNull final String runDirectory) {
        return new RunPathData(runDirectory, "bla-r", "bla-t", null, null);
    }
}
