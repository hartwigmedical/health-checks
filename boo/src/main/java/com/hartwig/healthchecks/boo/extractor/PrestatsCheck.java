package com.hartwig.healthchecks.boo.extractor;

import java.util.Arrays;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public enum PrestatsCheck {

    PRESTATS_PER_BASE_SEQUENCE_QUALITY("PRESTATS_PER_BASE_SEQUENCE_QUALITY"),
    PRESTATS_PER_TILE_SEQUENCE_QUALITY("PRESTATS_PER_TILE_SEQUENCE_QUALITY"),
    PRESTATS_PER_SEQUENCE_QUALITY_SCORES("PRESTATS_PER_SEQUENCE_QUALITY_SCORES"),
    PRESTATS_PER_BASE_SEQUENCE_CONTENT("PRESTATS_PER_BASE_SEQUENCE_CONTENT"),
    PRESTATS_PER_SEQUENCE_GC_CONTENT("PRESTATS_PER_SEQUENCE_GC_CONTENT"),
    PRESTATS_PER_BASE_N_CONTENT("PRESTATS_PER_BASE_N_CONTENT"),
    PRESTATS_SEQUENCE_LENGTH_DISTRIBUTION("PRESTATS_SEQUENCE_LENGTH_DISTRIBUTION"),
    PRESTATS_SEQUENCE_DUPLICATION_LEVELS("PRESTATS_SEQUENCE_DUPLICATION_LEVELS"),
    PRESTATS_OVERREPRESENTED_SEQUENCES("PRESTATS_OVERREPRESENTED_SEQUENCES"),
    PRESTATS_ADAPTER_CONTENT("PRESTATS_ADAPTER_CONTENT"),
    PRESTATS_KMER_CONTENT("PRESTATS_KMER_CONTENT"),
    PRESTATS_NUMBER_OF_READS("PRESTATS_NUMBER_OF_READS"),
    PRESTATS_BASIC_STATISTICS("Basic Statistics"),
    DUMMY("DummyCheckName");

    @NotNull
    private final String description;

    PrestatsCheck(@NotNull final String description) {
        this.description = description;
    }

    public static Optional<PrestatsCheck> getByDescription(@NotNull final String description) {
        return Arrays.stream(PrestatsCheck.values())
                        .filter(prestatsCheck -> prestatsCheck.description.equalsIgnoreCase(description)).findFirst();
    }

    @NotNull
    public String getDescription() {
        return description;
    }
}
