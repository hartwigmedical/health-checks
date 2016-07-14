package com.hartwig.healthchecks.common.predicate;

import java.util.function.Predicate;

public class VCFHeaderLinePredicate implements Predicate<String> {

    private static final String CHROM = "#CHROM";

    @Override
    public boolean test(final String line) {
        return line.startsWith(CHROM);
    }

}
