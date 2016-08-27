package com.hartwig.healthchecks.common.result;

import com.hartwig.healthchecks.common.checks.CheckType;

import org.jetbrains.annotations.NotNull;

public class ErrorResult extends BaseResult {

    private static final long serialVersionUID = 4309757413253950117L;

    @NotNull
    private final String error;
    @NotNull
    private final String message;

    public ErrorResult(@NotNull final CheckType checkType, @NotNull final String error,
            @NotNull final String message) {
        super(checkType);
        this.error = error;
        this.message = message;
    }

    @NotNull
    public String getError() {
        return error;
    }

    @NotNull
    public String getMessage() {
        return message;
    }
}
