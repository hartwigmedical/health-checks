package com.hartwig.healthchecks.nesbit.model;

public class VCFData {

    private final String info;

    private final VCFType type;

    public VCFData(final VCFType type, final String info) {
        super();
        this.type = type;
        this.info = info;
    }

    public VCFType getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }
}
