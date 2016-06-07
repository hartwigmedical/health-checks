package com.hartwig.healthchecks.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum CheckType {

    MAPPING,
    PRESTATS;

    private CheckType() {
    }

    public static Optional<CheckType> getByType(String type) {
        List<CheckType> types = Arrays.asList(CheckType.values());
        Optional<CheckType> returnType = types.stream()
                .filter(t -> t.toString().equalsIgnoreCase(type))
                .findFirst();

        return returnType;
    }
}
