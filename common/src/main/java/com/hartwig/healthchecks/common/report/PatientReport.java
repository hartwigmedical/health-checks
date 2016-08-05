package com.hartwig.healthchecks.common.report;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.checks.CheckType;

public class PatientReport extends BaseReport {

    private static final long serialVersionUID = -5744830259786248569L;

    @NotNull
    private final BaseDataReport patientData;

    public PatientReport(@NotNull final CheckType checkType, @NotNull final BaseDataReport patientData) {
        super(checkType);
        this.patientData = patientData;
    }

    @NotNull
    public BaseDataReport getPatientData() {
        return patientData;
    }
}
