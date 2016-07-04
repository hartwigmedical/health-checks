package com.hartwig.healthchecks.flint.report;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class SummaryMetricsReport extends BaseReport {

    private static final long serialVersionUID = 8849321997994924539L;

    @NotNull
    private final List<BaseDataReport> referenceSample;

    @NotNull
    private final List<BaseDataReport> tumorSample;

    public SummaryMetricsReport(final CheckType checkType, final List<BaseDataReport> referenceSample,
                    final List<BaseDataReport> tumorSample) {
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

    @Override
    public String toString() {
        return "SummaryMetricsReport [referenceSample=" + referenceSample + ", tumorSample=" + tumorSample + "]";
    }
}
