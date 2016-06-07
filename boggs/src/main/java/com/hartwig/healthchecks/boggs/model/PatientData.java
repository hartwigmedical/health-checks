package com.hartwig.healthchecks.boggs.model;

import com.hartwig.healthchecks.common.util.BaseConfig;
import com.hartwig.healthchecks.common.util.CheckType;
import org.jetbrains.annotations.NotNull;

public class PatientData extends BaseConfig {

    @NotNull
    private final SampleData refSample;
    @NotNull
    private final SampleData tumorSample;

    public PatientData(@NotNull CheckType type, @NotNull SampleData refSample, @NotNull SampleData tumorSample) {
        super(type);

        this.refSample = refSample;
        this.tumorSample = tumorSample;
    }

    @NotNull
    public SampleData getRefSample() {
        return refSample;
    }

    @NotNull
    public SampleData getTumorSample() {
        return tumorSample;
    }

    @Override
    public String toString() {
        return "PatientData{" +
                "getRefSample=" + refSample +
                ", getTumorSample=" + tumorSample +
                '}';
    }
}
