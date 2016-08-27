package com.hartwig.healthchecks.common.result;

import java.util.List;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;

import org.jetbrains.annotations.NotNull;

public class PatientResult extends BaseResult {

    private static final long serialVersionUID = -3227613309511119840L;

    @NotNull
    private final List<HealthCheck> refSampleChecks;
    @NotNull
    private final List<HealthCheck> tumorSampleChecks;

    public PatientResult(final CheckType checkType, @NotNull final List<HealthCheck> refSampleChecks,
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
