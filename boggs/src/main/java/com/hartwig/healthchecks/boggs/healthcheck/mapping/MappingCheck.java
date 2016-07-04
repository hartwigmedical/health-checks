package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import org.jetbrains.annotations.NotNull;

public enum MappingCheck {

    MAPPING_TOTAL("in total"),
    MAPPING_SECONDARY("secondary"),
    MAPPING_DUPLIVATES("duplicates"),
    MAPPING_MAPPED("mapped"),
    MAPPING_PROPERLY_PAIRED("properly paired"),
    MAPPING_MATE_MAPPED_DIFFERENT_CHR("with mate mapped to a different chr"),
    MAPPING_SINGLETON("singletons"),
    MAPPING_IS_ALL_READ("is all read"),
    DUMMY("DummyCheckName");

    @NotNull
    private final String description;

    MappingCheck(@NotNull final String description) {
        this.description = description;
    }

    @NotNull
    public String getDescription() {
        return description;
    }
}
