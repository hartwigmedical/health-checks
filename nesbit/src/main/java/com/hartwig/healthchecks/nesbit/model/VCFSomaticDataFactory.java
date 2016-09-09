package com.hartwig.healthchecks.nesbit.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;

public final class VCFSomaticDataFactory {

    private static final String VCF_COLUMN_SEPARATOR = "\t";

    private static final int INFO_COLUMN = 7;
    private static final int ALT_COLUMN = 4;
    private static final int REF_COLUMN = 3;
    private static final String MULTIPLE_ALTS_IDENTIFIER = ",";

    private static final String VCF_INFO_FIELD_SEPARATOR = ";";
    private static final String CALLER_ALGO_IDENTIFIER = "set=";
    private static final String CALLER_ALGO_START = "=";
    private static final String CALLER_ALGO_SEPARATOR = "-";
    private static final String CALLER_FILTERED_IDENTIFIER = "filterIn";
    private static final String CALLER_INTERSECTION_IDENTIFIER = "Intersection";

    private VCFSomaticDataFactory() {
    }

    @NotNull
    public static VCFSomaticData fromVCFLine(@NotNull final String line) {
        final String[] values = line.split(VCF_COLUMN_SEPARATOR);

        // TODO (KODU) Implement calculation of AF.
        return new VCFSomaticData(VCFExtractorFunctions.extractVCFType(values), extractCallers(values),
                new Random().nextDouble());
    }

    @NotNull
    private static List<String> extractCallers(@NotNull final String[] values) {
        final String info = values[INFO_COLUMN];

        final Optional<String> setValue = Arrays.stream(info.split(VCF_INFO_FIELD_SEPARATOR)).filter(
                infoLine -> infoLine.contains(CALLER_ALGO_IDENTIFIER)).map(
                infoLine -> infoLine.substring(infoLine.indexOf(CALLER_ALGO_START) + 1,
                        infoLine.length())).findFirst();
        assert setValue.isPresent();

        final String[] allCallers = setValue.get().split(CALLER_ALGO_SEPARATOR);
        List<String> finalCallers = Lists.newArrayList();
        if (allCallers.length > 0 && allCallers[0].equals(CALLER_INTERSECTION_IDENTIFIER)) {
            finalCallers.addAll(VCFConstants.ALL_CALLERS);
        } else {
            finalCallers.addAll(
                    Arrays.stream(allCallers).filter(caller -> !caller.startsWith(CALLER_FILTERED_IDENTIFIER)).collect(
                            Collectors.toList()));
        }
        return finalCallers;
    }
}
