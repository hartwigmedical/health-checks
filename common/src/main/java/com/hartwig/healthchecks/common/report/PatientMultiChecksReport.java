package com.hartwig.healthchecks.common.report;

import java.util.List;

import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class PatientMultiChecksReport extends BaseReport {

    private static final long serialVersionUID = 7396913735152649393L;

    private final List<BaseDataReport> patientData;

    public PatientMultiChecksReport(final CheckType checkType, final List<BaseDataReport> patientData) {
        super(checkType);
        this.patientData = patientData;
    }

    public List<BaseDataReport> getPatientData() {
        return patientData;
    }
}
