package com.hartwig.healthchecks.boggs.model.report;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class MappingReport extends BaseReport {
	@NotNull
	private final String externalId;
	@NotNull
	private final String totalSequences;
	@NotNull
	private final MappingDataReport mappingDataReport;

	public MappingReport(CheckType checkType, String externalId, String totalSequences,
			MappingDataReport mappingDataReport) {
		super(checkType);
		this.externalId = externalId;
		this.totalSequences = totalSequences;
		this.mappingDataReport = mappingDataReport;
	}

	public String getTotalSequences() {
		return totalSequences;
	}

	public MappingDataReport getMappingDataReport() {
		return mappingDataReport;
	}

	public String getExternalId() {
		return externalId;
	}


	@Override
	public String toString() {
		return "MappingReport [externalId=" + externalId + ", totalSequences=" + totalSequences + ", mappingDataReport="
				+ mappingDataReport + "]";
	}
}
