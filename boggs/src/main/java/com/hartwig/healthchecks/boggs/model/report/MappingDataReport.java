package com.hartwig.healthchecks.boggs.model.report;

import org.jetbrains.annotations.NotNull;

public class MappingDataReport {

	@NotNull
	private final String externalId;

	@NotNull
	private String mappedPercentage;
	@NotNull
	private String properlyPairedPercentage;
	@NotNull
	private String singletonPercentage;
	@NotNull
	private String mateMappedToDifferentChrPercentage;

	public MappingDataReport(String externalId) {
		this.externalId = externalId;
	}

	
	public void setMappedPercentage(String mappedPercentage) {
		this.mappedPercentage = mappedPercentage;
	}


	public void setProperlyPairedPercentage(String properlyPairedPercentage) {
		this.properlyPairedPercentage = properlyPairedPercentage;
	}


	public void setSingletonPercentage(String singletonPercentage) {
		this.singletonPercentage = singletonPercentage;
	}


	public void setMateMappedToDifferentChrPercentage(String mateMappedToDifferentChrPercentage) {
		this.mateMappedToDifferentChrPercentage = mateMappedToDifferentChrPercentage;
	}


	public String getExternalId() {
		return externalId;
	}

	public String getMappedPercentage() {
		return mappedPercentage;
	}

	public String getProperlyPairedPercentage() {
		return properlyPairedPercentage;
	}

	public String getSingletonPercentage() {
		return singletonPercentage;
	}

	public String getMateMappedToDifferentChrPercentage() {
		return mateMappedToDifferentChrPercentage;
	}

	@Override
	public String toString() {
		return "MappingReport [externalId=" + externalId + ", mappedPercentage=" + mappedPercentage
				+ ", properlyPairedPercentage=" + properlyPairedPercentage + ", singletonPercentage="
				+ singletonPercentage + ", mateMappedToDifferentChrPercentage=" + mateMappedToDifferentChrPercentage
				+ "]";
	}

}
