package com.hartwig.healthchecks.common.predicate;

import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

public class VCFHeaderLinePredicate implements Predicate<String> {

    private static final String HEADER_LINE_START_IDENTIFIER = "#CHROM";

    @Override
    public boolean test(@NotNull final String line) {
        return line.startsWith(HEADER_LINE_START_IDENTIFIER);
    }
}
