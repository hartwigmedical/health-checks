package com.hartwig.healthchecks.common.result;

import com.hartwig.healthchecks.common.checks.CheckType;

import org.jetbrains.annotations.NotNull;

public class ErrorResult extends BaseResult {

    private static final long serialVersionUID = 4309757413253950117L;

    @NotNull
    private final String error;

    public ErrorResult(@NotNull final CheckType checkType, @NotNull final String error) {
        super(checkType);
        this.error = error;
    }

    @NotNull
    public String getError() {
        return error;
    }
}
