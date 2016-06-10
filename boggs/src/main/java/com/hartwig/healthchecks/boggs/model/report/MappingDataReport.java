package com.hartwig.healthchecks.boggs.model.report;

import org.jetbrains.annotations.NotNull;

public class MappingDataReport {
	@NotNull
	private final Double mappedPercentage;
	@NotNull
	private final Double properlyPairedPercentage;
	@NotNull
	private final Double singletonPercentage;
	@NotNull
	private final Double mateMappedToDifferentChrPercentage;
	@NotNull
	private final Double proportionOfDuplicateRead;

	@NotNull
	private final boolean isAllReadsPresent;

	public MappingDataReport(Double mappedPercentage, Double properlyPairedPercentage, Double singletonPercentage,
			Double mateMappedToDifferentChrPercentage, Double proportionOfDuplicateRead, boolean isAllReadsPresent) {
		super();
		this.mappedPercentage = mappedPercentage;
		this.properlyPairedPercentage = properlyPairedPercentage;
		this.singletonPercentage = singletonPercentage;
		this.mateMappedToDifferentChrPercentage = mateMappedToDifferentChrPercentage;
		this.proportionOfDuplicateRead = proportionOfDuplicateRead;
		this.isAllReadsPresent = isAllReadsPresent;
	}

	public Double getMappedPercentage() {
		return mappedPercentage;
	}

	public Double getProperlyPairedPercentage() {
		return properlyPairedPercentage;
	}

	public Double getSingletonPercentage() {
		return singletonPercentage;
	}

	public Double getMateMappedToDifferentChrPercentage() {
		return mateMappedToDifferentChrPercentage;
	}

	public Double getProportionOfDuplicateRead() {
		return proportionOfDuplicateRead;
	}

	public boolean isAllReadsPresent() {
		return isAllReadsPresent;
	}

	@Override
	public String toString() {
		return "MappingDataReport [mappedPercentage=" + mappedPercentage + ", properlyPairedPercentage="
				+ properlyPairedPercentage + ", singletonPercentage=" + singletonPercentage
				+ ", mateMappedToDifferentChrPercentage=" + mateMappedToDifferentChrPercentage
				+ ", proportionOfDuplicateRead=" + proportionOfDuplicateRead + ", isAllReadsPresent="
				+ isAllReadsPresent + "]";
	}
}
