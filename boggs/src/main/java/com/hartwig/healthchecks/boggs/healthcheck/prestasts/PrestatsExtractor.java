package com.hartwig.healthchecks.boggs.healthcheck.prestasts;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.boggs.extractor.BoggsExtractor;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.boggs.reader.ZipFileReader;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class PrestatsExtractor extends BoggsExtractor {

    protected static final String SUMMARY_FILE_NAME = "summary.txt";

    private static final int EXPECTED_LINE_LENGTH = 3;

    private static final long MIN_TOTAL_SQ = 85000000L;

    private static final String FOUND_FAILS_MSG = "NOT OK: %s has status FAIL for Patient %s";

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
                    throws IOException, EmptyFileException {
        final List<BaseDataReport> refSampleData = getSampleData(runDirectory, SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumorSampleData = getSampleData(runDirectory, SAMPLE_PREFIX, TUM_SAMPLE_SUFFIX);

        return new PrestatsReport(CheckType.PRESTATS, refSampleData, tumorSampleData);
    }

    @NotNull
    private List<BaseDataReport> getSampleData(@NotNull final String runDirectory, @NotNull final String prefix,
                    @NotNull final String suffix) throws IOException, EmptyFileException {
        final Optional<Path> samplePath = getFilesPath(runDirectory, prefix, suffix);
        final String samplePatientId = samplePath.get().getFileName().toString();
        final List<BaseDataReport> sampleData = getSummaryFilesData(samplePath.get(), samplePatientId);
        final BaseDataReport sampleFastq = getFastqFilesData(samplePath.get(), samplePatientId);
        sampleData.add(sampleFastq);
        logPrestatsReport(sampleData);
        return sampleData;
    }

    @NotNull
    private List<BaseDataReport> getSummaryFilesData(@NotNull final Path pathToCheck, @NotNull final String patientId)
                    throws IOException, EmptyFileException {
        final Path path = new File(pathToCheck + File.separator + QC_STATS + File.separator).toPath();

        final Map<String, List<BaseDataReport>> data = extractSummaryDataFromZipFiles(path, patientId);

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
    private Map<String, List<BaseDataReport>> extractSummaryDataFromZipFiles(final Path pathToCheck,
                    final String patientId) throws IOException {

        final List<Path> zipFiles = getAllZipFilesPaths(pathToCheck);

        final List<String> allLines = readAllLines(zipFiles);

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
    private List<String> readAllLines(final List<Path> zipFiles) {
        return zipFiles.stream().map(path -> zipFileReader.readFileFromZip(path.toString(), SUMMARY_FILE_NAME))
                        .flatMap(Collection::stream).collect(toList());
    }

    @NotNull
    private List<Path> getAllZipFilesPaths(final Path pathToCheck) throws IOException {
        return Files.walk(pathToCheck).filter(path -> path.getFileName().toString().endsWith(ZIP_FILES_SUFFIX)).sorted()
                        .collect(toCollection(ArrayList<Path>::new));
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
    private BaseDataReport getFastqFilesData(@NotNull final Path pathToCheck, @NotNull final String patientId)
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

    private void logPrestatsReport(final List<BaseDataReport> prestatsDataReport) {
        prestatsDataReport.forEach((prestatsData) -> {
            if (prestatsData.getValue().equalsIgnoreCase(FAIL)) {
                LOGGER.info(String.format(FOUND_FAILS_MSG, prestatsData.getCheckName(), prestatsData.getPatientId()));
            }
        });
    }
}
