package com.hartwig.healthchecks.flint.extractor;

import org.jetbrains.annotations.NotNull;

enum CoverageCheck {
    COVERAGE_MEAN("MEAN_COVERAGE", 1),
    COVERAGE_SD("SD_COVERAGE", 2),
    COVERAGE_MEDIAN("MEDIAN_COVERAGE", 3),
    COVERAGE_PCT_EXC_MAPQ("PCT_EXC_MAPQ", 5),
    COVERAGE_PCT_EXC_DUPE("PCT_EXC_DUPE", 6),
    COVERAGE_PCT_EXC_UNPAIRED("PCT_EXC_UNPAIRED", 7),
    COVERAGE_PCT_EXC_BASEQ("PCT_EXC_BASEQ", 8),
    COVERAGE_PCT_EXC_OVERLAP("PCT_EXC_OVERLAP", 9),
    COVERAGE_PCT_EXC_TOTAL("PCT_EXC_TOTAL", 11),;

    @NotNull
    private final String fieldName;
    private final int index;

    CoverageCheck(@NotNull final String fieldName, final int index) {
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
