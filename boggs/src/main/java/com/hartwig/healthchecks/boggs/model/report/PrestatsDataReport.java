package com.hartwig.healthchecks.boggs.model.report;

import org.jetbrains.annotations.NotNull;

public class PrestatsDataReport {

    private final String checkName;
    private final String status;

    public PrestatsDataReport(@NotNull final String status, @NotNull final String checkName) {
        this.status = status;
        this.checkName = checkName;
    }

    @NotNull
    public String getCheckName() {
        return checkName;
    }

    @NotNull
    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "PrestatsDataReport [checkName=" + checkName + ", status=" + status + "]";
    }
}
