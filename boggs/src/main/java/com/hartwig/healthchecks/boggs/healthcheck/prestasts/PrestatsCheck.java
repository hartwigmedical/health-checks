package com.hartwig.healthchecks.boggs.healthcheck.prestasts;

import java.util.Arrays;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public enum PrestatsCheck {

  PRESTATS_PER_BASE_SEQUENCE_QUALITY("Per base sequence quality"), 
  PRESTATS_BASIC_STATISTICS("Basic Statistics"), 
  PRESTATS_PER_TILE_SEQUENCE_QUALITY("Per tile sequence quality"), 
  PRESTATS_PER_SEQUENCE_QUALITY_SCORES("Per sequence quality scores"), 
  PRESTATS_PER_BASE_SEQUENCE_CONTENT("Per base sequence content"), 
  PRESTATS_PER_SEQUENCE_GC_CONTENT("Per sequence GC content"), 
  PRESTATS_PER_BASE_N_CONTENT("Per base N content"), 
  PRESTATS_SEQUENCE_LENGTH_DISTRIBUTION("Sequence Length Distribution"), 
  PRESTATS_SEQUENCE_DUPLICATION_LEVELS("Sequence Duplication Levels"), 
  PRESTATS_OVERREPRESENTED_SEQUENCES("Overrepresented sequences"), 
  PRESTATS_ADAPTER_CONTENT("Adapter Content"), 
  PRESTATS_KMER_CONTENT("Kmer Content"),
  PRESTATS_NUMBER_OF_READS("Total Sequences"),
  DUMMY("DummyCheckName");

  private final String description;

  PrestatsCheck(@NotNull final String description) {
    this.description = description;
  }

  public static Optional<PrestatsCheck> getByDescription(@NotNull final String description) {
    return Arrays.asList(PrestatsCheck.values()).stream().filter(
        prestatsCheck -> prestatsCheck.description.equalsIgnoreCase(description)).findFirst();
  }

  public String getDescription() {
    return this.description;
  }
}