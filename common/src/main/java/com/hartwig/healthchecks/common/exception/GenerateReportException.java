package com.hartwig.healthchecks.common.exception;

import org.jetbrains.annotations.NotNull;

public class GenerateReportException extends Exception {

    public GenerateReportException(@NotNull final String message) {
        super(message);
    }
}
