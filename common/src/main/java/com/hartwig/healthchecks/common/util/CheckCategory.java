package com.hartwig.healthchecks.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public enum CheckCategory {

    BOGGS(new String[] { "summary.txt", "fastqc_data.txt", "_dedup.realigned.flagstat" }),
    DUMMY(new String[] { "summary.txt", "fastqc_data.txt", "_dedup.realigned.flagstat" });

    private String[] files;

    CheckCategory(@NotNull final String... files) {
        this.files = files;
    }

    public static Optional<CheckCategory> getByCategory(@NotNull final String typeToCheck) {
        List<CheckCategory> types = Arrays.asList(CheckCategory.values());
        Optional<CheckCategory> returnType = types.stream().filter(
                type -> type.toString().equalsIgnoreCase(typeToCheck)).findFirst();

        return returnType;
    }

    @NotNull public String[] getFiles() {
        return files;
    }
}
