package com.hartwig.healthchecks.common.predicate;

import java.util.function.Predicate;

public class VCFPassDataLinePredicate implements Predicate<String> {

    private static final String SEPERATOR_REGEX = "\t";

    private static final int FILTER_INDEX = 6;

    private static final String DOT = ".";

    private static final String PASS = "PASS";

    private static final String HASH = "#";

    @Override
    public boolean test(final String line) {
        boolean isData = false;
        if (!line.startsWith(HASH)) {
            final String[] values = line.split(SEPERATOR_REGEX);
            final String filterValue = values[FILTER_INDEX];
            if (filterValue.equals(PASS) || filterValue.equals(DOT)) {
                isData = true;
            }
        }
        return isData;
    }

}
