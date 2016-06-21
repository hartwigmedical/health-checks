package com.hartwig.healthchecks.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum CheckCategory {

    BOGGS(new String[]{"summary.txt", "fastqc_data.txt", "_dedup.realigned.flagstat"}),
    DUMMY(new String[]{"summary.txt", "fastqc_data.txt", "_dedup.realigned.flagstat"});

    private String[] files;

    CheckCategory(String[] files) {
        this.files = files;
    }

    public static Optional<CheckCategory> getByCategory(String type) {
        List<CheckCategory> types = Arrays.asList(CheckCategory.values());
        Optional<CheckCategory> returnType = types.stream()
                .filter(t -> t.toString().equalsIgnoreCase(type))
                .findFirst();

        return returnType;
    }

    public String[] getFiles() {
        return files;
    }
}
