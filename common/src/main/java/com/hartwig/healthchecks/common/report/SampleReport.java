package com.hartwig.healthchecks.common.report;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class SampleReport extends BaseReport {

    private static final long serialVersionUID = -3227613309511119840L;

    @NotNull
    private final List<BaseDataReport> referenceSample;

    @NotNull
    private final List<BaseDataReport> tumorSample;

    public SampleReport(final CheckType checkType, final List<BaseDataReport> referenceSample,
                    final List<BaseDataReport> tumorSample) {
        super(checkType);
        this.referenceSample = referenceSample;
        this.tumorSample = tumorSample;
    }

    public List<BaseDataReport> getReferenceSample() {
        return referenceSample;
    }

    public List<BaseDataReport> getTumorSample() {
        return tumorSample;
    }

    @Override
    public String toString() {
        return "SampleReport [referenceSample=" + referenceSample + ", tumorSample=" + tumorSample + "]";
    }

}
