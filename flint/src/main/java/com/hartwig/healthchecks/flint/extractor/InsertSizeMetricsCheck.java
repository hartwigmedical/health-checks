package com.hartwig.healthchecks.flint.extractor;

public enum InsertSizeMetricsCheck {
    MAPPING_MEDIAN_INSERT_SIZE("MEDIAN_INSERT_SIZE", 0),
    MAPPING_WIDTH_OF_70_PERCENT("WIDTH_OF_70_PERCENT", 15),;

    private final String fieldName;

    private final int index;

    InsertSizeMetricsCheck(final String fieldName, final int index) {
        this.fieldName = fieldName;
        this.index = index;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getIndex() {
        return index;
    }

}
