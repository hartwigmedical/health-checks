package com.hartwig.healthchecks.nesbit.model;

public class VCFGermlineData {

    private final String refData;

    private final String tumData;

    private final VCFType type;

    public VCFGermlineData(final VCFType type, final String refData, final String tumData) {
        super();
        this.refData = refData;
        this.tumData = tumData;
        this.type = type;
    }

    public VCFType getType() {
        return type;
    }

    public String getRefData() {
        return refData;
    }

    public String getTumData() {
        return tumData;
    }

}
