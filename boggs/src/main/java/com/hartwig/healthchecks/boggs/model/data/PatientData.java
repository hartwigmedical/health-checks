package com.hartwig.healthchecks.boggs.model.data;

import org.jetbrains.annotations.NotNull;

public class PatientData {

    @NotNull
    private final SampleData refSample;
    @NotNull
    private final SampleData tumorSample;

    public PatientData(@NotNull final SampleData refSample, @NotNull final SampleData tumorSample) {
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
