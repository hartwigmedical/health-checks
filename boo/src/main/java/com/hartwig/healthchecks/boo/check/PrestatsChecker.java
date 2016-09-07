package com.hartwig.healthchecks.boo.check;

import static java.util.stream.Collectors.groupingBy;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.checks.HealthCheckFunctions;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.reader.ZipFilesReader;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.PatientResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("WeakerAccess")
@ResourceWrapper(type = CheckType.PRESTATS)
public class PrestatsChecker implements HealthChecker {

    private static final Logger LOGGER = LogManager.getLogger(PrestatsChecker.class);

    @VisibleForTesting
    static final String PASS = "PASS";
    @VisibleForTesting
    static final String WARN = "WARN";
    @VisibleForTesting
    static final String FAIL = "FAIL";
    @VisibleForTesting
    static final String MISS = "MISS";

    private static final String FASTQ_BASE_DIRECTORY = "QCStats";
    private static final String FASTQC_CHECKS_FILE_NAME = "summary.txt";
    private static final String FASTQC_CHECKS_SEPARATOR = "\t";
    private static final int FASTQC_CHECKS_EXPECTED_PARTS_PER_LINE = 3;

    private static final String EMPTY_FILES_ERROR = "File %s was found empty in path -> %s";

    @NotNull
    private final ZipFilesReader zipFileReader = new ZipFilesReader();

    public PrestatsChecker() {
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
    public CheckType checkType() {
        return CheckType.PRESTATS;
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
            throws IOException, EmptyFileException {
        final List<String> allLines = zipFileReader.readAllLinesFromZips(basePath, FASTQC_CHECKS_FILE_NAME);
        final Map<String, List<HealthCheck>> data = getFastqcCheckData(allLines, sampleId);

        if (data == null || data.isEmpty()) {
            LOGGER.error(String.format(EMPTY_FILES_ERROR, FASTQC_CHECKS_FILE_NAME, basePath));
            throw new EmptyFileException(FASTQC_CHECKS_FILE_NAME, basePath);
        }

        final List<HealthCheck> finalList = Lists.newArrayList();
        for (PrestatsCheck check : PrestatsCheck.values()) {
            if (check != PrestatsCheck.PRESTATS_NUMBER_OF_READS) {
                List<HealthCheck> checkData = data.get(check.toString());
                if (checkData != null && checkData.size() > 0) {
                    Optional<HealthCheck> worstReport = checkData.stream().min((isStatusWorse()));
                    // KODU: Safe to do since we checked that checkData contains > 0 elements.
                    assert worstReport.isPresent();
                    finalList.add(worstReport.get());
                } else {
                    finalList.add(new HealthCheck(sampleId, check.toString(), MISS));
                }
            }
        }

        return finalList;
    }

    @NotNull
    private static Map<String, List<HealthCheck>> getFastqcCheckData(@NotNull final List<String> allLines,
            @NotNull final String sampleId) throws IOException {
        return allLines.stream().map(line -> {
            final String[] values = line.trim().split(FASTQC_CHECKS_SEPARATOR);
            HealthCheck prestatsDataReport = null;
            if (values.length == FASTQC_CHECKS_EXPECTED_PARTS_PER_LINE) {
                final String status = values[0];
                final Optional<PrestatsCheck> check = PrestatsCheck.getByDescription(values[1]);
                if (check.isPresent()) {
                    prestatsDataReport = new HealthCheck(sampleId, check.get().toString(), status);
                }
            }
            return prestatsDataReport;
        }).filter(prestatsDataReport -> prestatsDataReport != null).collect(groupingBy(HealthCheck::getCheckName));
    }

    @NotNull
    private static Comparator<HealthCheck> isStatusWorse() {
        return (firstData, secondData) -> {
            final String firstStatus = firstData.getValue();
            final String secondStatus = secondData.getValue();
            int status = 1;
            if (firstStatus.equals(secondStatus)) {
                status = 0;
            } else if (FAIL.equals(firstStatus)) {
                status = -1;
            } else if (WARN.equals(firstStatus) && PASS.equals(secondStatus)) {
                status = -1;
            }
            return status;
        };
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
