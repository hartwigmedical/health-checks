package com.hartwig.healthchecks.common.report;

import java.util.List;

import com.hartwig.healthchecks.common.checks.CheckType;

import org.jetbrains.annotations.NotNull;

public class PatientMultiChecksReport extends BaseReport {

    private static final long serialVersionUID = 7396913735152649393L;

    @NotNull
    private final List<BaseDataReport> patientData;

    public PatientMultiChecksReport(@NotNull final CheckType checkType,
            @NotNull final List<BaseDataReport> patientData) {
        super(checkType);
        this.patientData = patientData;
    }

    @NotNull
    public List<BaseDataReport> getPatientData() {
        return patientData;
    }
}
