package com.hartwig.healthchecks.boggs.healthcheck.prestasts;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

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
import com.hartwig.healthchecks.boggs.model.report.PrestatsDataReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.boggs.reader.ZipFileReader;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.CheckType;

public class PrestatsExtractor extends BoggsExtractor {

    protected static final String PASS = "PASS";

    protected static final String WARN = "WARN";

    protected static final String FAIL = "FAIL";

    protected static final String SUMMARY_FILE_NAME = "summary.txt";

    private static final int EXPECTED_LINE_LENGTH = 3;

    private static final int ONE = 1;

    private static final int NEGATIVE_ONE = -1;

    private static final int ZERO = 0;

    private static final long MIN_TOTAL_SQ = 85000000L;

    private static final Logger LOGGER = LogManager.getLogger(ZipFileReader.class);

    @NotNull
    private final ZipFileReader zipFileReader;

    public PrestatsExtractor(@NotNull final ZipFileReader zipFileReader) {
        super();
        this.zipFileReader = zipFileReader;
    }

    public PrestatsReport extractFromRunDirectory(@NotNull final String runDirectory)
                    throws IOException, EmptyFileException {
        final List<PrestatsDataReport> refSampleData = getSampleData(runDirectory, SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);
        final List<PrestatsDataReport> tumSampleData = getSampleData(runDirectory, SAMPLE_PREFIX, TUM_SAMPLE_SUFFIX);

        final PrestatsReport prestatsData = new PrestatsReport(CheckType.PRESTATS);
        prestatsData.addReferenceData(refSampleData);
        prestatsData.addTumorData(tumSampleData);
        return prestatsData;
    }

    private List<PrestatsDataReport> getSampleData(@NotNull final String runDirectory, @NotNull final String prefix,
                    @NotNull final String suffix) throws IOException, EmptyFileException {
        final Optional<Path> samplePath = getFilesPath(runDirectory, prefix, suffix);
        final String samplePatientId = samplePath.get().getFileName().toString();
        final List<PrestatsDataReport> sampleData = getSummaryFilesData(samplePath.get(), samplePatientId);
        final PrestatsDataReport sampleFastq = getFastqFilesData(samplePath.get(), samplePatientId);
        sampleData.add(sampleFastq);
        return sampleData;
    }

    private List<PrestatsDataReport> getSummaryFilesData(@NotNull final Path pathToCheck,
                    @NotNull final String patientId) throws IOException, EmptyFileException {

        final Map<PrestatsCheck, List<PrestatsDataReport>> data = extractSummaryDataFromZipFiles(pathToCheck,
                        patientId);

        final List<PrestatsDataReport> prestatsDataReports = data.values().stream().map(prestatsDataReportList -> {
            return prestatsDataReportList.stream().min(isStatusWorse()).get();
        }).collect(toList());
        if (prestatsDataReports == null || prestatsDataReports.isEmpty()) {
            LOGGER.error(String.format(EMPTY_FILES_ERROR, pathToCheck));
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, pathToCheck));
        }
        return prestatsDataReports;
    }

    private Map<PrestatsCheck, List<PrestatsDataReport>> extractSummaryDataFromZipFiles(final Path pathToCheck,
                    final String patientId) throws IOException {

        final List<Path> zipFiles = getAllZipFilesPaths(pathToCheck);

        final List<String> allLines = readAllLines(zipFiles);

        return allLines.stream().map(line -> {
            final String[] values = line.trim().split(SEPERATOR_REGEX);
            PrestatsDataReport prestatsDataReport = null;
            if (values.length == EXPECTED_LINE_LENGTH) {
                final String status = values[0];
                final Optional<PrestatsCheck> check = PrestatsCheck.getByDescription(values[1]);
                if (check.isPresent()) {
                    prestatsDataReport = new PrestatsDataReport(patientId, status, check.get());
                }
            }
            return prestatsDataReport;
        }).filter(prestatsDataReport -> prestatsDataReport != null)
                        .collect(groupingBy(PrestatsDataReport::getCheckName));
    }

    private List<String> readAllLines(final List<Path> zipFiles) {
        return zipFiles.stream().map(path -> {
            List<String> lines = null;
            lines = zipFileReader.readFileFromZip(path.toString(), SUMMARY_FILE_NAME);
            if (lines == null) {
                lines = new ArrayList<>();
            }
            return lines;
        }).flatMap(Collection::stream).collect(toList());
    }

    private List<Path> getAllZipFilesPaths(final Path pathToCheck) throws IOException {
        return Files.walk(pathToCheck)
                        .filter(path -> path.getFileName().toString().endsWith(ZIP_FILES_SUFFIX)
                                        && path.getParent().getFileName().toString().equals("QCStats"))
                        .sorted().collect(toCollection(ArrayList<Path>::new));
    }

    private Comparator<PrestatsDataReport> isStatusWorse() {
        final Comparator<PrestatsDataReport> isStatusWorse = new Comparator<PrestatsDataReport>() {
            @Override
            public int compare(@NotNull final PrestatsDataReport firstData,
                            @NotNull final PrestatsDataReport secondData) {
                final String firstStatus = firstData.getStatus();
                final String secondStatus = secondData.getStatus();
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

    private PrestatsDataReport getFastqFilesData(@NotNull final Path pathToCheck, @NotNull final String patientId)
                    throws IOException, EmptyFileException {
        final Long totalSequences = sumOfTotalSequences(pathToCheck, zipFileReader);
        PrestatsDataReport prestatsDataReport = null;
        if (totalSequences != null) {
            String status = PASS;
            if (totalSequences < MIN_TOTAL_SQ) {
                status = FAIL;
            }
            prestatsDataReport = new PrestatsDataReport(patientId, status, PrestatsCheck.PRESTATS_NUMBER_OF_READS);
        }

        if (prestatsDataReport == null) {
            LOGGER.error(String.format(EMPTY_FILES_ERROR, pathToCheck));
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, pathToCheck));
        }
        return prestatsDataReport;
    }
}