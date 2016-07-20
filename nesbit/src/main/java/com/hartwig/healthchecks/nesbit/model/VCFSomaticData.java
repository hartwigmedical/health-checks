package com.hartwig.healthchecks.nesbit.model;

public class VCFSomaticData {

    private final String info;

    private final VCFType type;

    public VCFSomaticData(final VCFType type, final String info) {
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
