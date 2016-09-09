package com.hartwig.healthchecks.tony.check;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.MalformedFileException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.path.PathPrefixSuffixFinder;
import com.hartwig.healthchecks.common.io.reader.FileReader;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.MultiValueResult;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("WeakerAccess")
@ResourceWrapper(type = CheckType.COPYNUMBER)
public class CopynumberChecker implements HealthChecker {

    private static final String COPYNUMBER_BASE_DIRECTORY = "copyNumber";
    private static final String COPYNUMBER_SAMPLE_CONNECTOR = "_";
    private static final String COPYNUMBER_ALGO_DIRECTORY = "freec";
    private static final String COPYNUMBER_SUFFIX = ".bam_CNVs";

    private static final String FIELD_SEPARATOR = "\t";
    private static final int START_FIELD_INDEX = 1;
    private static final int END_FIELD_INDEX = 2;
    private static final int LOSS_GAIN_FIELD_INDEX = 4;

    private static final String GAIN_IDENTIFIER = "gain";
    private static final String LOSS_IDENTIFIER = "loss";
    private static final String GAIN_LOSS_ERROR = "Could not parse gain/loss identifier: %s";

    public CopynumberChecker() {
    }

    @NotNull
    @Override
    public BaseResult run(@NotNull final RunContext runContext) throws IOException, HealthChecksException {
        final Path copynumberPath = PathPrefixSuffixFinder.build().findPath(getBasePath(runContext),
                runContext.tumorSample(), COPYNUMBER_SUFFIX);
        final List<String> copynumberLines = FileReader.build().readLines(copynumberPath);
        long totalGain = 0;
        long totalLoss = 0;
        for (final String line : copynumberLines) {
            final String[] parts = line.split(FIELD_SEPARATOR);

            final long startIndex = Long.valueOf(parts[START_FIELD_INDEX].trim());
            final long endIndex = Long.valueOf(parts[END_FIELD_INDEX].trim());
            final long change = endIndex - startIndex;

            final String lossOrGain = parts[LOSS_GAIN_FIELD_INDEX].trim();
            switch (lossOrGain) {
                case GAIN_IDENTIFIER:
                    totalGain += change;
                    break;
                case LOSS_IDENTIFIER:
                    totalLoss += change;
                    break;
                default:
                    throw new MalformedFileException(String.format(GAIN_LOSS_ERROR, lossOrGain));
            }
        }

        final HealthCheck gainCheck = new HealthCheck(runContext.tumorSample(),
                CopynumberCheck.COPYNUMBER_GENOME_GAIN.toString(), String.valueOf(totalGain));
        final HealthCheck lossCheck = new HealthCheck(runContext.tumorSample(),
                CopynumberCheck.COPYNUMBER_GENOME_LOSS.toString(), String.valueOf(totalLoss));

        return new MultiValueResult(checkType(), Lists.newArrayList(gainCheck, lossCheck));
    }

    @NotNull
    @Override
    public CheckType checkType() {
        return CheckType.COPYNUMBER;
    }

    @NotNull
    private static String getBasePath(@NotNull final RunContext runContext) {
        return runContext.runDirectory() + File.separator + COPYNUMBER_BASE_DIRECTORY + File.separator
                + runContext.refSample() + COPYNUMBER_SAMPLE_CONNECTOR + runContext.tumorSample() + File.separator
                + COPYNUMBER_ALGO_DIRECTORY;
    }
}
