package com.hartwig.healthchecks.flint.extractor;

public enum CoverageCheck {
    COVERAGE_MEAN("MEAN_COVERAGE", 1),;

    private final String fieldName;

    private final int index;

    CoverageCheck(final String fieldName, final int index) {
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
