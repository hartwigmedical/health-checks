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
        return this.patientId;
    }

    @NotNull
    public PrestatsCheck getCheckName() {
        return this.checkName;
    }

    @NotNull
    public String getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return "PrestatsDataReport [externalId=" + this.patientId + ", checkName=" + this.checkName + ", status="
                + this.status + "]";
    }
}
