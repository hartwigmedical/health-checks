package com.hartwig.healthchecks.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public enum CheckType {

    MAPPING, PRESTATS;

    @NotNull public static Optional<CheckType> getByType(String typeToCheck) {
        final List<CheckType> types = Arrays.asList(CheckType.values());
        final Optional<CheckType> returnType = types.stream().filter(
                type -> type.toString().equalsIgnoreCase(typeToCheck)).findFirst();

        return returnType;
    }
}
