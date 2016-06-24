package com.hartwig.healthchecks.boggs.model.report;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsCheck;

public class PrestatsDataReport {

    @NotNull
    private final String patientId;

    @NotNull
    private final PrestatsCheck checkName;

    @NotNull
    private final String status;

    public PrestatsDataReport(@NotNull final String patientId, @NotNull final String status,
                    @NotNull final PrestatsCheck checkName) {
        this.patientId = patientId;
        this.status = status;
        this.checkName = checkName;
    }

    @NotNull
    public String getPatientId() {
        return patientId;
    }

    @NotNull
    public PrestatsCheck getCheckName() {
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