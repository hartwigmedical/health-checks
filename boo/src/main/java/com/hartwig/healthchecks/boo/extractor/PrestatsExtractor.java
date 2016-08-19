package com.hartwig.healthchecks.boo.extractor;

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
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.extractor.AbstractTotalSequenceExtractor;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.reader.ZipFilesReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class PrestatsExtractor extends AbstractTotalSequenceExtractor {

    private static final Logger LOGGER = LogManager.getLogger(PrestatsExtractor.class);

    @VisibleForTesting
    static final String PASS = "PASS";
    @VisibleForTesting
    static final String WARN = "WARN";
    @VisibleForTesting
    static final String FAIL = "FAIL";
    @VisibleForTesting
    static final String MISS = "MISS";

    private static final String PRESTATS_BASE_DIR = "QCStats";

    private static final String FASTQC_CHECKS_FILE_NAME = "summary.txt";
    private static final String FASTQC_CHECKS_SEPARATOR = "\t";
    private static final int FASTQC_CHECKS_EXPECTED_PARTS_PER_LINE = 3;

    private static final String EMPTY_FILES_ERROR = "File %s was found empty in path -> %s";

    @NotNull
    private final RunContext runContext;
    @NotNull
    private final ZipFilesReader zipFileReader = new ZipFilesReader();

    public PrestatsExtractor(@NotNull final RunContext runContext) {
        this.runContext = runContext;
    }

    @Override
    @NotNull
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final List<BaseDataReport> refSampleData = getSampleData(runContext.runDirectory(), runContext.refSample());
        final List<BaseDataReport> tumorSampleData = getSampleData(runContext.runDirectory(),
                runContext.tumorSample());

        return new SampleReport(CheckType.PRESTATS, refSampleData, tumorSampleData);
    }

    @NotNull
    private List<BaseDataReport> getSampleData(@NotNull final String runDirectory, @NotNull final String sampleId)
            throws IOException, EmptyFileException {
        final String basePath = getBasePathForSample(runDirectory, sampleId);

        final List<BaseDataReport> fastqcChecks = extractFastqcChecks(basePath, sampleId);
        final BaseDataReport totalSequenceCheck = extractTotalSequenceCheck(basePath, sampleId);

        fastqcChecks.add(totalSequenceCheck);
        BaseDataReport.log(LOGGER, fastqcChecks);
        return fastqcChecks;
    }

    @NotNull
    private List<BaseDataReport> extractFastqcChecks(@NotNull final String basePath, @NotNull final String sampleId)
            throws IOException, EmptyFileException {
        final List<String> allLines = zipFileReader.readAllLinesFromZips(basePath, FASTQC_CHECKS_FILE_NAME);
        final Map<String, List<BaseDataReport>> data = getFastqcCheckData(allLines, sampleId);

        if (data == null || data.isEmpty()) {
            LOGGER.error(String.format(EMPTY_FILES_ERROR, FASTQC_CHECKS_FILE_NAME, basePath));
            throw new EmptyFileException(FASTQC_CHECKS_FILE_NAME, basePath);
        }

        final List<BaseDataReport> finalList = Lists.newArrayList();
        for (PrestatsCheck check : PrestatsCheck.values()) {
            if (check != PrestatsCheck.PRESTATS_NUMBER_OF_READS) {
                List<BaseDataReport> checkData = data.get(check.toString());
                if (checkData != null && checkData.size() > 0) {
                    Optional<BaseDataReport> worstReport = checkData.stream().min((isStatusWorse()));
                    // KODU: Safe to do since we checked that checkData contains > 0 elements.
                    assert worstReport.isPresent();
                    finalList.add(worstReport.get());
                } else {
                    finalList.add(new BaseDataReport(sampleId, check.toString(), MISS));
                }
            }
        }

        return finalList;
    }

    @NotNull
    private static Map<String, List<BaseDataReport>> getFastqcCheckData(@NotNull final List<String> allLines,
            @NotNull final String sampleId) throws IOException {
        return allLines.stream().map(line -> {
            final String[] values = line.trim().split(FASTQC_CHECKS_SEPARATOR);
            BaseDataReport prestatsDataReport = null;
            if (values.length == FASTQC_CHECKS_EXPECTED_PARTS_PER_LINE) {
                final String status = values[0];
                final Optional<PrestatsCheck> check = PrestatsCheck.getByDescription(values[1]);
                if (check.isPresent()) {
                    prestatsDataReport = new BaseDataReport(sampleId, check.get().toString(), status);
                }
            }
            return prestatsDataReport;
        }).filter(prestatsDataReport -> prestatsDataReport != null).collect(groupingBy(BaseDataReport::getCheckName));
    }

    @NotNull
    private static Comparator<BaseDataReport> isStatusWorse() {
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
    private BaseDataReport extractTotalSequenceCheck(@NotNull final String basePath, @NotNull final String sampleId)
            throws IOException, EmptyFileException {
        final long totalSequences = sumOfTotalSequencesFromFastQC(basePath, zipFileReader);
        if (totalSequences == 0) {
            throw new EmptyFileException(FASTQC_DATA_FILE_NAME, basePath);
        }
        return new BaseDataReport(sampleId, PrestatsCheck.PRESTATS_NUMBER_OF_READS.toString(),
                Long.toString(totalSequences));
    }

    @NotNull
    private static String getBasePathForSample(@NotNull final String runDirectory, @NotNull final String sampleId) {
        return runDirectory + File.separator + sampleId + File.separator + PRESTATS_BASE_DIR;
    }
}
