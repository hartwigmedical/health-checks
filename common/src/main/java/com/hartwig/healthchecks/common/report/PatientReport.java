package com.hartwig.healthchecks.common.report;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class PatientReport extends BaseReport {

    private static final long serialVersionUID = -5744830259786248569L;

    private final BaseDataReport patientData;

    public PatientReport(final CheckType checkType, final BaseDataReport patientData) {
        super(checkType);
        this.patientData = patientData;
    }

    @NotNull
    public BaseDataReport getPatientData() {
        return patientData;
    }
}
