package com.hartwig.healthchecks.common.util;

import org.jetbrains.annotations.NotNull;

public class ErrorReport extends BaseReport {

    private static final long serialVersionUID = 4309757413253950117L;

    @NotNull
    private final String error;

    @NotNull
    private final String message;

    public ErrorReport(@NotNull final CheckType checkType, @NotNull final String error, @NotNull final String message) {
        super(checkType);
        this.error = error;
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ErrorReport [error=" + error + ", message=" + message + "]";
    }
}