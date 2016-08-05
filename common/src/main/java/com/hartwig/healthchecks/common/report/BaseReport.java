package com.hartwig.healthchecks.common.report;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.checks.CheckType;

public class BaseReport implements Serializable {

    private static final long serialVersionUID = -4752339157661751000L;

    @NotNull
    private final CheckType checkType;

    public BaseReport(@NotNull final CheckType checkType) {
        this.checkType = checkType;
    }

    @NotNull
    public CheckType getCheckType() {
        return checkType;
    }
}
