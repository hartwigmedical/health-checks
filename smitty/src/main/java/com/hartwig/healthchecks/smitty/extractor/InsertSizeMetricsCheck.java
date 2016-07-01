package com.hartwig.healthchecks.smitty.extractor;

public enum InsertSizeMetricsCheck {
    MAPPING_MEDIAN_INSERT_SIZE("MAPPING_INSERT_SIZE_MEDIAN", "MEDIAN_INSERT_SIZE", 0),
    MAPPING_WIDTH_OF_70_PERCENT("MAPPING_INSERT_SIZE_WIDTH_OF_70_PERCENT", "WIDTH_OF_70_PERCENT", 15),;

    private final String name;

    private final String fieldName;

    private final int index;

    InsertSizeMetricsCheck(final String name, final String fieldName, final int index) {
        this.name = name;
        this.fieldName = fieldName;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getIndex() {
        return index;
    }

}
