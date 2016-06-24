package com.hartwig.healthchecks.boggs.model.report;

import org.jetbrains.annotations.NotNull;

public class BaseDataReport {

    @NotNull
    private final String patientId;

    @NotNull
    private final String checkName;

    @NotNull
    private final String value;

    public BaseDataReport(@NotNull final String patientId, @NotNull final String checkName,
            @NotNull final String value) {
        this.patientId = patientId;
        this.checkName = checkName;
        this.value = value;
    }

    @NotNull
    public String getPatientId() {
        return patientId;
    }

    @NotNull
    public String getCheckName() {
        return checkName;
    }

    @NotNull
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "BaseDataReport{" +
                "patientId='" + patientId + '\'' +
                ", checkName=" + checkName +
                ", value='" + value + '\'' +
                '}';
    }
}