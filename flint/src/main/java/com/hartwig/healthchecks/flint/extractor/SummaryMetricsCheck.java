package com.hartwig.healthchecks.flint.extractor;

public enum SummaryMetricsCheck {
    MAPPING_PF_INDEL_RATE("MAPPING_PF_INDEL_RATE", 14);

    private final String name;

    private final int index;

    SummaryMetricsCheck(final String name, final int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

}
