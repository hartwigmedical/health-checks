package com.hartwig.healthchecks.flint.extractor;

import org.jetbrains.annotations.NotNull;

enum InsertSizeMetricsCheck {
    MAPPING_MEDIAN_INSERT_SIZE("MEDIAN_INSERT_SIZE", 0),
    MAPPING_WIDTH_OF_70_PERCENT("WIDTH_OF_70_PERCENT", 15),;

    @NotNull
    private final String fieldName;
    private final int index;

    InsertSizeMetricsCheck(@NotNull final String fieldName, final int index) {
        this.fieldName = fieldName;
        this.index = index;
    }

    @NotNull
    public String getFieldName() {
        return fieldName;
    }

    public int getIndex() {
        return index;
    }
}
