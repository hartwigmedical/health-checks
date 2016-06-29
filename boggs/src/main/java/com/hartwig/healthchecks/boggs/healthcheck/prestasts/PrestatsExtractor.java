package com.hartwig.healthchecks.boggs.healthcheck.prestasts;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.boggs.extractor.AbstractBoggsExtractor;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.boggs.reader.ZipFileReader;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class PrestatsExtractor extends AbstractBoggsExtractor {

    protected static final String SUMMARY_FILE_NAME = "summary.txt";

    private static final int EXPECTED_LINE_LENGTH = 3;

    private static final long MIN_TOTAL_SQ = 85000000L;

    private static final Logger LOGGER = LogManager.getLogger(PrestatsExtractor.class);

    @NotNull
    private final ZipFileReader zipFileReader;

    public PrestatsExtractor(@NotNull final ZipFileReader zipFileReader) {
        super();
        this.zipFileReader = zipFileReader;
    }

    @Override
    @NotNull
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
                    throws IOException, HealthChecksException {
        final List<BaseDataReport> refSampleData = getSampleData(runDirectory, SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumorSampleData = getSampleData(runDirectory, SAMPLE_PREFIX, TUM_SAMPLE_SUFFIX);
        return new PrestatsReport(CheckType.PRESTATS, refSampleData, tumorSampleData);
    }

    @NotNull
    private List<BaseDataReport> getSampleData(@NotNull final String runDirectory, @NotNull final String prefix,
                    @NotNull final String suffix) throws IOException, EmptyFileException {
        final Path samplePath = zipFileReader.getZipFilesPath(runDirectory, prefix, suffix);
        final String samplePatientId = samplePath.getFileName().toString();
        final List<BaseDataReport> sampleData = extractSummaryData(samplePath, samplePatientId);
        final BaseDataReport sampleFastq = extractFastqData(samplePath, samplePatientId);
        sampleData.add(sampleFastq);
        logBaseDataReports(sampleData);
        return sampleData;
    }

    @NotNull
    private List<BaseDataReport> extractSummaryData(@NotNull final Path samplePath, @NotNull final String patientId)
                    throws IOException, EmptyFileException {
        final Path path = new File(samplePath + File.separator + QC_STATS + File.separator).toPath();
        final List<String> allLines = zipFileReader.readAllLinesFromZips(path, SUMMARY_FILE_NAME);
        final Map<String, List<BaseDataReport>> data = getSummaryData(allLines, patientId);
        final List<BaseDataReport> prestatsDataReports = data.values().stream().map(prestatsDataReportList -> {
            return prestatsDataReportList.stream().min(isStatusWorse()).get();
        }).collect(toList());
        if (prestatsDataReports == null || prestatsDataReports.isEmpty()) {
            LOGGER.error(String.format(EMPTY_FILES_ERROR, SUMMARY_FILE_NAME, path));
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, SUMMARY_FILE_NAME, path));
        }
        return prestatsDataReports;
    }

    @NotNull
    private Map<String, List<BaseDataReport>> getSummaryData(final List<String> allLines, final String patientId)
                    throws IOException {
        return allLines.stream().map(line -> {
            final String[] values = line.trim().split(SEPERATOR_REGEX);
            BaseDataReport prestatsDataReport = null;
            if (values.length == EXPECTED_LINE_LENGTH) {
                final String status = values[0];
                final Optional<PrestatsCheck> check = PrestatsCheck.getByDescription(values[1]);
                if (check.isPresent()) {
                    prestatsDataReport = new BaseDataReport(patientId, check.get().getDescription(), status);
                }
            }
            return prestatsDataReport;
        }).filter(prestatsDataReport -> prestatsDataReport != null).collect(groupingBy(BaseDataReport::getCheckName));
    }

    @NotNull
    private Comparator<BaseDataReport> isStatusWorse() {
        final Comparator<BaseDataReport> isStatusWorse = new Comparator<BaseDataReport>() {

            @Override
            public int compare(@NotNull final BaseDataReport firstData, @NotNull final BaseDataReport secondData) {
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
            }
        };
        return isStatusWorse;
    }

    @NotNull
    private BaseDataReport extractFastqData(@NotNull final Path pathToCheck, @NotNull final String patientId)
                    throws IOException, EmptyFileException {
        final Long totalSequences = sumOfTotalSequences(pathToCheck, zipFileReader);
        if (totalSequences == ZERO_DOUBLE_VALUE) {
            final String errorMessage = String.format(EMPTY_FILES_ERROR, FASTQC_DATA_FILE_NAME, pathToCheck);
            LOGGER.error(errorMessage);
            throw new EmptyFileException(errorMessage);
        }
        String status = PASS;
        if (totalSequences < MIN_TOTAL_SQ) {
            status = FAIL;
        }
        return new BaseDataReport(patientId, PrestatsCheck.PRESTATS_NUMBER_OF_READS.getDescription(), status);
    }
}
