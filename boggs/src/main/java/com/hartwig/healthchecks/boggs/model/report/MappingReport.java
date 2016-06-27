package com.hartwig.healthchecks.boggs.model.report;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class MappingReport extends BaseReport {

    private static final long serialVersionUID = 7647060563039702736L;

    @NotNull
    private final List<BaseDataReport> referenceSample;

    @NotNull
    private final List<BaseDataReport> tumorSample;

    public MappingReport(final CheckType checkType, final List<BaseDataReport> referenceSample,
                    final List<BaseDataReport> tumorSample) {
        super(checkType);
        this.referenceSample = referenceSample;
        this.tumorSample = tumorSample;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public List<BaseDataReport> getReferenceSample() {
        return referenceSample;
    }

    public List<BaseDataReport> getTumorSample() {
        return tumorSample;
    }

    @Override
    public String toString() {
        return "MappingReport [referenceSample=" + referenceSample + ", tumorSample=" + tumorSample + "]";
    }
}
