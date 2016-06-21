package com.hartwig.healthchecks.common.exception;

import org.jetbrains.annotations.NotNull;

public class EmptyFileException extends Exception {

    public EmptyFileException(@NotNull final String message) {
        super(message);
    }
}
