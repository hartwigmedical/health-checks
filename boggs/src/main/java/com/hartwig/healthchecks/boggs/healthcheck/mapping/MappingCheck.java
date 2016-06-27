package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import java.util.Arrays;
import java.util.Optional;

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

    private final String description;

    MappingCheck(@NotNull final String description) {
        this.description = description;
    }

    public static Optional<MappingCheck> getByDescription(@NotNull final String description) {
        return Arrays.asList(MappingCheck.values())
                .stream()
                .filter(mappingcheck -> mappingcheck.description.equalsIgnoreCase(description))
                .findFirst();
    }

    public String getDescription() {
        return description;
    }
}