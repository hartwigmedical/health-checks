package com.hartwig.healthchecks.boggs.model.report;

import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

import org.jetbrains.annotations.NotNull;

public class MappingReport extends BaseReport {

    @NotNull
    private final String externalId;
    @NotNull
    private final String totalSequences;
    @NotNull
    private final MappingDataReport mappingDataReport;

    public MappingReport(@NotNull final CheckType checkType, @NotNull final String externalId,
            @NotNull final String totalSequences, @NotNull final MappingDataReport mappingDataReport) {
        super(checkType);
        this.externalId = externalId;
        this.totalSequences = totalSequences;
        this.mappingDataReport = mappingDataReport;
    }

    @NotNull
    public String getTotalSequences() {
        return totalSequences;
    }

    @NotNull
    public MappingDataReport getMappingDataReport() {
        return mappingDataReport;
    }

    @NotNull
    public String getExternalId() {
        return externalId;
    }

    @Override
    public String toString() {
        return "MappingReport [externalId=" + externalId + ", totalSequences=" + totalSequences
                + ", mappingDataReport=" + mappingDataReport + "]";
    }
}
