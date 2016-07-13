package com.hartwig.healthchecks.nesbit.model;

public class VCFData {

    private final String ref;

    private final String alt;

    private final String info;

    private final VCFType type;

    public VCFData(final VCFType type, final String ref, final String alt, final String info) {
        super();
        this.type = type;
        this.ref = ref;
        this.alt = alt;
        this.info = info;
    }

    public VCFType getType() {
        return type;
    }

    public String getRef() {
        return ref;
    }

    public String getAlt() {
        return alt;
    }

    public String getInfo() {
        return info;
    }
}
