package com.hartwig.healthchecks.common.checks;

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
    BOO;

    @NotNull
    public static Optional<CheckCategory> getByCategory(@NotNull final String typeToCheck) {
        final List<CheckCategory> types = Arrays.asList(CheckCategory.values());

        return types.stream().filter(type -> type.toString().equalsIgnoreCase(typeToCheck)).findFirst();
    }
}
