package com.hartwig.healthchecks.common.predicate;

import java.util.function.Predicate;

public class VCFDataLinePredicate implements Predicate<String> {

    private static final String HASH = "#";

    @Override
    public boolean test(final String line) {
        return !line.startsWith(HASH);
    }

}
