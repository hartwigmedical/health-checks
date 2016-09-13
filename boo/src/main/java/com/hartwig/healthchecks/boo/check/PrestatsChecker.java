package com.hartwig.healthchecks.boo.check;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.checks.HealthCheckFunctions;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.reader.ZipFilesReader;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.MultiValueResult;
import com.hartwig.healthchecks.common.result.PatientResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("WeakerAccess")
@ResourceWrapper(type = CheckType.PRESTATS)
public class PrestatsChecker implements HealthChecker {

    private static final Logger LOGGER = LogManager.getLogger(PrestatsChecker.class);

    private static final String FASTQ_BASE_DIRECTORY = "QCStats";
    private static final String FASTQC_CHECKS_FILE_NAME = "summary.txt";
    private static final String FASTQC_CHECKS_SEPARATOR = "\t";
    private static final int FASTQC_CHECKS_EXPECTED_PARTS_PER_LINE = 3;

    private static final String PRESTATS_CHECK_FORMAT = "%s_%s";

    @NotNull
    private final ZipFilesReader zipFileReader = new ZipFilesReader();

    public PrestatsChecker() {
    }

    @NotNull
    @Override
    public CheckType checkType() {
        return CheckType.PRESTATS;
    }

    @Override
    @NotNull
    public BaseResult run(@NotNull final RunContext runContext) throws IOException, HealthChecksException {
        final List<HealthCheck> refSampleData = getSampleData(runContext.runDirectory(), runContext.refSample());
        final List<HealthCheck> tumorSampleData = getSampleData(runContext.runDirectory(), runContext.tumorSample());

        return new PatientResult(checkType(), refSampleData, tumorSampleData);
    }

    @NotNull
    @Override
    public BaseResult errorResult(@NotNull final RunContext runContext) {
        return new MultiValueResult(checkType(), Lists.newArrayList());
    }

    @NotNull
    private List<HealthCheck> getSampleData(@NotNull final String runDirectory, @NotNull final String sampleId)
            throws IOException, HealthChecksException {
        final String basePath = getBasePathForSample(runDirectory, sampleId);

        final List<HealthCheck> fastqcChecks = extractFastqcChecks(basePath, sampleId);
        final HealthCheck totalSequenceCheck = extractTotalSequenceCheck(basePath, sampleId);

        fastqcChecks.add(totalSequenceCheck);
        HealthCheck.log(LOGGER, fastqcChecks);
        return fastqcChecks;
    }

    @NotNull
    private List<HealthCheck> extractFastqcChecks(@NotNull final String basePath, @NotNull final String sampleId)
            throws IOException {
        final List<String> allLines = zipFileReader.readAllLinesFromZips(basePath, FASTQC_CHECKS_FILE_NAME);
        final Map<PrestatsCheck, List<PrestatsCheckValue>> data = getFastqcCheckData(allLines);

        final List<HealthCheck> finalList = Lists.newArrayList();

        for (PrestatsCheck check : PrestatsCheck.valuesToIncludeInCount()) {
            final Map<PrestatsCheckValue, Integer> valueCounts = initializeToZeroCounts();
            final List<PrestatsCheckValue> values = data.get(check);
            for (PrestatsCheckValue value : values) {
                valueCounts.put(value, valueCounts.get(value) + 1);
            }

            for (Map.Entry<PrestatsCheckValue, Integer> countedValue : valueCounts.entrySet()) {
                finalList.add(new HealthCheck(sampleId, toCheckName(check, countedValue.getKey()),
                        Integer.toString(countedValue.getValue())));
            }
        }

        return finalList;
    }

    @NotNull
    @VisibleForTesting
    static String toCheckName(@NotNull final PrestatsCheck check, @NotNull final PrestatsCheckValue value) {
        return String.format(PRESTATS_CHECK_FORMAT, check.toString(), value.toString());
    }

    @NotNull
    private static Map<PrestatsCheck, List<PrestatsCheckValue>> getFastqcCheckData(
            @NotNull final List<String> allLines) {
        final Map<PrestatsCheck, List<PrestatsCheckValue>> results = initializeToEmptyLists();
        for (String line : allLines) {
            final String[] values = line.trim().split(FASTQC_CHECKS_SEPARATOR);
            if (values.length == FASTQC_CHECKS_EXPECTED_PARTS_PER_LINE) {
                final Optional<PrestatsCheck> check = PrestatsCheck.getByDescription(values[1]);
                final PrestatsCheckValue value = PrestatsCheckValue.valueOf(values[0]);
                if (check.isPresent()) {
                    List<PrestatsCheckValue> currentValues = results.get(check.get());
                    currentValues.add(value);
                    results.put(check.get(), currentValues);
                }
            }
        }
        return results;
    }

    @NotNull
    private static Map<PrestatsCheck, List<PrestatsCheckValue>> initializeToEmptyLists() {
        final Map<PrestatsCheck, List<PrestatsCheckValue>> emptyLists = Maps.newHashMap();
        for (PrestatsCheck check : PrestatsCheck.valuesToIncludeInCount()) {
            emptyLists.put(check, Lists.newArrayList());
        }
        return emptyLists;
    }

    @NotNull
    private static Map<PrestatsCheckValue, Integer> initializeToZeroCounts() {
        final Map<PrestatsCheckValue, Integer> zeroCounts = Maps.newHashMap();
        for (PrestatsCheckValue value : PrestatsCheckValue.values()) {
            zeroCounts.put(value, 0);
        }
        return zeroCounts;
    }

    @NotNull
    private HealthCheck extractTotalSequenceCheck(@NotNull final String basePath, @NotNull final String sampleId)
            throws IOException, HealthChecksException {
        final long totalSequences = HealthCheckFunctions.sumOfTotalSequencesFromFastQC(basePath, zipFileReader);

        return new HealthCheck(sampleId, PrestatsCheck.PRESTATS_NUMBER_OF_READS.toString(),
                Long.toString(totalSequences));
    }

    @NotNull
    private static String getBasePathForSample(@NotNull final String runDirectory, @NotNull final String sampleId) {
        return runDirectory + File.separator + sampleId + File.separator + FASTQ_BASE_DIRECTORY;
    }
}
