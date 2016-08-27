package com.hartwig.healthchecks.common.data;

import java.util.List;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.report.HealthCheck;

import org.jetbrains.annotations.NotNull;

public class PatientReport extends BaseReport {

    private static final long serialVersionUID = -3227613309511119840L;

    @NotNull
    private final List<HealthCheck> refSampleChecks;
    @NotNull
    private final List<HealthCheck> tumorSampleChecks;

    public PatientReport(final CheckType checkType, @NotNull final List<HealthCheck> refSampleChecks,
            @NotNull final List<HealthCheck> tumorSampleChecks) {
        super(checkType);
        this.refSampleChecks = refSampleChecks;
        this.tumorSampleChecks = tumorSampleChecks;
    }

    @NotNull
    public List<HealthCheck> getRefSampleChecks() {
        return refSampleChecks;
    }

    @NotNull
    public List<HealthCheck> getTumorSampleChecks() {
        return tumorSampleChecks;
    }
}
