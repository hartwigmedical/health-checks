package com.hartwig.healthchecks.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public enum CheckCategory {

    BOGGS,
    SMITTY,
    FLINT,
    NESBIT,
    ROZ,
    BILE,
    DUMMY;

    public static Optional<CheckCategory> getByCategory(@NotNull final String typeToCheck) {
        final List<CheckCategory> types = Arrays.asList(CheckCategory.values());
        final Optional<CheckCategory> returnType = types.stream()
                        .filter(type -> type.toString().equalsIgnoreCase(typeToCheck)).findFirst();

        return returnType;
    }
}
