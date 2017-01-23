package com.hartwig.healthchecks.tony.check;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.ErrorHandlingChecker;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.checks.HealthCheckConstants;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.MalformedFileException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.path.PathRegexFinder;
import com.hartwig.healthchecks.common.io.reader.FileReader;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.MultiValueResult;

@SuppressWarnings("WeakerAccess")
@ResourceWrapper(type = CheckType.COPYNUMBER)
public class CopynumberChecker extends ErrorHandlingChecker implements HealthChecker {

    private static final Logger LOGGER = LogManager.getLogger(CopynumberChecker.class);

    // KODU: copynumber data is stored in {run}/copyNumber/{sampleR}_{sampleT}/freec/{sampleT}<>.bam_CNVs
    private static final String COPYNUMBER_BASE_DIRECTORY = "copyNumber";
    private static final String COPYNUMBER_SAMPLE_CONNECTOR = "_";
    private static final String COPYNUMBER_ALGO_DIRECTORY = "freec";
    private static final String COPYNUMBER_REGEX = "%s_.*(?<!_normal)_CNVs$";
    private static final String COPYNUMBER_RATIO_REGEX = "%s_.*(?<!_normal)_ratio.txt$";

    private static final String FIELD_SEPARATOR = "\t";
    private static final int START_FIELD_INDEX = 1;
    private static final int END_FIELD_INDEX = 2;
    private static final int LOSS_GAIN_FIELD_INDEX = 4;

    private static final String GAIN_IDENTIFIER = "gain";
    private static final String LOSS_IDENTIFIER = "loss";
    private static final String NEUTRAL_IDENTIFIER = "neutral";
    private static final String GAIN_LOSS_ERROR = "Could not parse gain/loss identifier: %s";

    public CopynumberChecker() {
    }

    @NotNull
    @Override
    public CheckType checkType() {
        return CheckType.COPYNUMBER;
    }

    @NotNull
    @Override
    public BaseResult tryRun(@NotNull final RunContext runContext) throws IOException, HealthChecksException {
        long totalGain = 0;
        long totalLoss = 0;
        for (final String line : copynumberLines(runContext)) {
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
                case NEUTRAL_IDENTIFIER:
                    break;
                default:
                    throw new MalformedFileException(String.format(GAIN_LOSS_ERROR, lossOrGain));
            }
        }

        return toMultiValueResult(runContext, String.valueOf(totalGain), String.valueOf(totalLoss));
    }

    @NotNull
    private static List<String> copynumberLines(@NotNull final RunContext runContext)
            throws IOException, EmptyFileException {
        final Path copynumberPath = PathRegexFinder.build()
                                                   .findPath(getBasePath(runContext),
                                                             String.format(COPYNUMBER_REGEX,
                                                                           runContext.tumorSample()));
        try {
            return FileReader.build().readLines(copynumberPath);
        } catch (EmptyFileException e) {
            // if the CNV is empty (but exists) and the ratio file exists, there is no problem (just no CNVs found)
            final Path ratioPath = PathRegexFinder.build()
                                                  .findPath(getBasePath(runContext),
                                                            String.format(COPYNUMBER_RATIO_REGEX,
                                                                          runContext.tumorSample()));
            FileReader.build().readLines(ratioPath);
            return Collections.emptyList();
        }
    }

    @NotNull
    @Override
    public BaseResult errorRun(@NotNull final RunContext runContext) {
        return toMultiValueResult(runContext, HealthCheckConstants.ERROR_VALUE, HealthCheckConstants.ERROR_VALUE);
    }

    @NotNull
    private BaseResult toMultiValueResult(@NotNull final RunContext runContext, @NotNull final String totalGain,
            @NotNull final String totalLoss) {
        final HealthCheck gainCheck = new HealthCheck(runContext.tumorSample(),
                CopynumberCheck.COPYNUMBER_GENOME_GAIN.toString(), totalGain);
        final HealthCheck lossCheck = new HealthCheck(runContext.tumorSample(),
                CopynumberCheck.COPYNUMBER_GENOME_LOSS.toString(), totalLoss);
        final List<HealthCheck> checks = Lists.newArrayList(gainCheck, lossCheck);
        HealthCheck.log(LOGGER, checks);
        return new MultiValueResult(checkType(), checks);
    }

    @NotNull
    private static String getBasePath(@NotNull final RunContext runContext) {
        return runContext.runDirectory() + File.separator + COPYNUMBER_BASE_DIRECTORY + File.separator
                + runContext.refSample() + COPYNUMBER_SAMPLE_CONNECTOR + runContext.tumorSample() + File.separator
                + COPYNUMBER_ALGO_DIRECTORY;
    }
}
