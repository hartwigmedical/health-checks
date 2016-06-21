package com.hartwig.healthchecks.boggs.model.report;

import org.jetbrains.annotations.NotNull;

public class PrestatsDataReport {

    @NotNull
    private final String patientId;
    @NotNull
    private final String checkName;
    @NotNull
    private final String status;

    public PrestatsDataReport(@NotNull final String patientId, @NotNull final String status,
            @NotNull final String checkName) {
        this.patientId = patientId;
        this.status = status;
        this.checkName = checkName;
    }

    public String getExternalId() {
        return patientId;
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
        return "PrestatsDataReport [externalId=" + patientId + ", checkName=" + checkName + ", status=" + status + "]";
    }
}
