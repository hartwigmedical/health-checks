package com.hartwig.healthchecks.common.result;

import java.io.Serializable;

import com.hartwig.healthchecks.common.checks.CheckType;

import org.jetbrains.annotations.NotNull;

public class BaseResult implements Serializable {

    private static final long serialVersionUID = -4752339157661751000L;

    @NotNull
    private final CheckType checkType;

    public BaseResult(@NotNull final CheckType checkType) {
        this.checkType = checkType;
    }

    @NotNull
    public CheckType getCheckType() {
        return checkType;
    }
}
