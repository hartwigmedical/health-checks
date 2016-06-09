package com.hartwig.healthchecks.boggs.model.report;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class MappingReport extends BaseReport {

	@NotNull
	private final MappingDataReport refData;
	@NotNull
	private final MappingDataReport tumorData;

	public MappingReport(CheckType checkType, MappingDataReport refData, MappingDataReport tumorData) {
		super(checkType);
		this.refData = refData;
		this.tumorData = tumorData;
	}

	public MappingDataReport getRefData() {
		return refData;
	}

	public MappingDataReport getTumorData() {
		return tumorData;
	}

	@Override
	public String toString() {
		return "MappingReport [refData=" + refData + ", tumorData=" + tumorData + "]";
	}
}
