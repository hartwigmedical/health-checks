package com.hartwig.healthchecks.boggs.model.report;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class PrestatsReport extends BaseReport {

    private static final long serialVersionUID = 3588650481644358694L;

    @NotNull
    private final List<BaseDataReport> referenceSample;

    @NotNull
    private final List<BaseDataReport> tumorSample;

    public PrestatsReport(final CheckType checkType, final List<BaseDataReport> referenceSample,
                    final List<BaseDataReport> tumorSample) {
        super(checkType);
        this.referenceSample = referenceSample;
        this.tumorSample = tumorSample;
    }

    @NotNull
    public List<BaseDataReport> getTumorSample() {
        return tumorSample;
    }

    @NotNull
    public List<BaseDataReport> getReferenceSample() {
        return referenceSample;
    }

    @Override
    public String toString() {
        return "PrestatsReport [referenceSample=" + referenceSample + ", tumorSample=" + tumorSample + "]";
    }
}
