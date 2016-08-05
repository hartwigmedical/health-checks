package com.hartwig.healthchecks.common.report;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.checks.CheckType;

public class SampleReport extends BaseReport {

    private static final long serialVersionUID = -3227613309511119840L;

    @NotNull
    private final List<BaseDataReport> referenceSample;
    @NotNull
    private final List<BaseDataReport> tumorSample;

    public SampleReport(final CheckType checkType, @NotNull final List<BaseDataReport> referenceSample,
                    @NotNull final List<BaseDataReport> tumorSample) {
        super(checkType);
        this.referenceSample = referenceSample;
        this.tumorSample = tumorSample;
    }

    @NotNull
    public List<BaseDataReport> getReferenceSample() {
        return referenceSample;
    }

    @NotNull
    public List<BaseDataReport> getTumorSample() {
        return tumorSample;
    }
}
