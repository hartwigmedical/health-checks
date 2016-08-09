package com.hartwig.healthchecks.boo.extractor;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.extractor.AbstractTotalSequenceExtractor;
import com.hartwig.healthchecks.common.io.path.SamplePathFinder;
import com.hartwig.healthchecks.common.io.reader.ZipFilesReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class PrestatsExtractor extends AbstractTotalSequenceExtractor {

    static final String PASS = "PASS";
    static final String WARN = "WARN";
    static final String FAIL = "FAIL";
    static final String SUMMARY_FILE_NAME = "summary.txt";

    private static final int EXPECTED_LINE_LENGTH = 3;
    private static final long MIN_TOTAL_SQ = 85000000L;

    private static final Logger LOGGER = LogManager.getLogger(PrestatsExtractor.class);

    @NotNull
    private final ZipFilesReader zipFileReader;
    @NotNull
    private final SamplePathFinder samplePathFinder;

    public PrestatsExtractor(@NotNull final ZipFilesReader zipFileReader,
            @NotNull final SamplePathFinder samplePathFinder) {
        super();
        this.zipFileReader = zipFileReader;
        this.samplePathFinder = samplePathFinder;
    }

    @Override
    @NotNull
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final List<BaseDataReport> refSampleData = getSampleData(runDirectory, SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumorSampleData = getSampleData(runDirectory, SAMPLE_PREFIX, TUM_SAMPLE_SUFFIX);
        return new SampleReport(CheckType.PRESTATS, refSampleData, tumorSampleData);
    }

    @NotNull
    private List<BaseDataReport> getSampleData(@NotNull final String runDirectory, @NotNull final String prefix,
            @NotNull final String suffix) throws IOException, EmptyFileException {
        final Path samplePath = samplePathFinder.findPath(runDirectory, prefix, suffix);
        final String sampleId = samplePath.getFileName().toString();
        final List<BaseDataReport> sampleData = extractSummaryData(samplePath, sampleId);
        final BaseDataReport sampleFastq = extractFastqData(samplePath, sampleId);
        sampleData.add(sampleFastq);
        logBaseDataReports(sampleData);
        return sampleData;
    }

    @NotNull
    private List<BaseDataReport> extractSummaryData(@NotNull final Path samplePath, @NotNull final String sampleId)
            throws IOException, EmptyFileException {
        final Path path = new File(samplePath + File.separator + QC_STATS + File.separator).toPath();
        final List<String> allLines = zipFileReader.readAllLinesFromZips(path, SUMMARY_FILE_NAME);
        final Map<String, List<BaseDataReport>> data = getSummaryData(allLines, sampleId);
        final List<BaseDataReport> prestatsDataReports = data.values().stream().map(
                prestatsDataReportList -> prestatsDataReportList.stream().min(isStatusWorse()).get()).collect(
                toList());
        if (prestatsDataReports == null || prestatsDataReports.isEmpty()) {
            LOGGER.error(String.format(EMPTY_FILES_ERROR, SUMMARY_FILE_NAME, path));
            throw new EmptyFileException(SUMMARY_FILE_NAME, path.toString());
        }
        return prestatsDataReports;
    }

    @NotNull
    private static Map<String, List<BaseDataReport>> getSummaryData(@NotNull final List<String> allLines,
            @NotNull final String sampleId) throws IOException {
        return allLines.stream().map(line -> {
            final String[] values = line.trim().split(SEPARATOR_REGEX);
            BaseDataReport prestatsDataReport = null;
            if (values.length == EXPECTED_LINE_LENGTH) {
                final String status = values[0];
                final Optional<PrestatsCheck> check = PrestatsCheck.getByDescription(values[1]);
                if (check.isPresent()) {
                    prestatsDataReport = new BaseDataReport(sampleId, check.get().getDescription(), status);
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
            int status = ONE;
            if (firstStatus.equals(secondStatus)) {
                status = ZERO;
            } else if (FAIL.equals(firstStatus)) {
                status = NEGATIVE_ONE;
            } else if (WARN.equals(firstStatus) && PASS.equals(secondStatus)) {
                status = NEGATIVE_ONE;
            }
            return status;
        };
    }

    @NotNull
    private BaseDataReport extractFastqData(@NotNull final Path pathToCheck, @NotNull final String sampleId)
            throws IOException, EmptyFileException {
        final long totalSequences = sumOfTotalSequencesFromFastQC(pathToCheck, zipFileReader);
        if (totalSequences == ZERO_DOUBLE_VALUE) {
            throw new EmptyFileException(FASTQC_DATA_FILE_NAME, pathToCheck.toString());
        }
        String status = PASS;
        if (totalSequences < MIN_TOTAL_SQ) {
            status = FAIL;
        }
        return new BaseDataReport(sampleId, PrestatsCheck.PRESTATS_NUMBER_OF_READS.getDescription(), status);
    }
}
