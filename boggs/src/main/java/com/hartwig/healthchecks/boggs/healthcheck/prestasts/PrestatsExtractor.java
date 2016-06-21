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

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.boggs.extractor.BoggsExtractor;
import com.hartwig.healthchecks.boggs.model.report.PrestatsDataReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.CheckType;

public class PrestatsExtractor extends BoggsExtractor {

    protected static final String PASS = "PASS";

    protected static final String WARN = "WARN";

    protected static final String FAIL = "FAIL";

    private static final int ONE = 1;

    private static final int NEGATIVE_ONE = -1;

    private static final int ZERO = 0;

    private static final String SUMMARY_FILE_NAME = "summary.txt";

    private static final long MIN_TOTAL_SQ = 85000000l;

    public PrestatsReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, EmptyFileException {
        final Optional<Path> pathToCheck = getFilesPath(runDirectory, SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);

        final String patientId = pathToCheck.get().getFileName().toString();

        final List<PrestatsDataReport> summaryData = getSummaryFilesData(pathToCheck.get(), patientId);

        final PrestatsDataReport fastqcData = getfastqFilesData(pathToCheck.get(), patientId);

        if (summaryData == null || summaryData.isEmpty() || fastqcData == null) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, runDirectory));
        }

        final PrestatsReport prestatsData = new PrestatsReport(CheckType.PRESTATS);
        prestatsData.addAllData(summaryData);
        prestatsData.addData(fastqcData);

        return prestatsData;
    }

    private List<PrestatsDataReport> getSummaryFilesData(@NotNull final Path pathToCheck,
            @NotNull final String patientId) throws IOException, EmptyFileException {

        final List<Path> zipFiles = Files.walk(pathToCheck)
                .filter(path -> path.getFileName().toString().endsWith(ZIP_FILES_SUFFIX)).sorted()
                .collect(toCollection(ArrayList<Path>::new));

        final Comparator<PrestatsDataReport> isStatusWorse = new Comparator<PrestatsDataReport>() {
            @Override
            public int compare(@NotNull final PrestatsDataReport firstData,
                    @NotNull final PrestatsDataReport secondData) {
                final String firstStatus = firstData.getStatus();
                final String secondStatus = secondData.getStatus();
                int status = ONE;
                if (firstStatus.equals(secondStatus)) {
                    status = ZERO;
                } else if (FAIL.equals(firstStatus) || WARN.equals(firstStatus)) {
                    status = NEGATIVE_ONE;
                }
                return status;
            }
        };

        final Map<PrestatsCheck, List<PrestatsDataReport>> data = zipFiles.stream().map(path -> {
            List<String> lines = null;
            lines = getLinesFromFile(path, SUMMARY_FILE_NAME);
            if (lines == null) {
                lines = new ArrayList<>();
            }
            return lines;
        }).flatMap(Collection::stream).map(line -> {
            final String[] values = line.split(SEPERATOR_REGEX);
            PrestatsDataReport prestatsDataReport = null;
            if (values.length == 3) {
                final String status = values[0];
                final Optional<PrestatsCheck> check = PrestatsCheck.getByDescription(values[1]);
                if (check.isPresent()) {
                    prestatsDataReport = new PrestatsDataReport(patientId, status, check.get());
                }
            }
            return prestatsDataReport;
        }).filter(prestatsDataReport -> prestatsDataReport != null)
                .collect(groupingBy((PrestatsDataReport::getCheckName)));

        return data.values().stream().map(prestatsDataReportList -> {
            return prestatsDataReportList.stream().min(isStatusWorse).get();
        }).collect(toList());
    }

    private PrestatsDataReport getfastqFilesData(@NotNull final Path pathToCheck, @NotNull final String patientId)
            throws IOException, EmptyFileException {
        final Long totalSequences = sumOfTotalSequences(pathToCheck);
        PrestatsDataReport prestatsDataReport = null;
        if (totalSequences != null) {
            String status = PASS;
            if (totalSequences < MIN_TOTAL_SQ) {
                status = FAIL;
            }
            prestatsDataReport = new PrestatsDataReport(patientId, status, PrestatsCheck.PRESTATS_NUMBER_OF_READS);
        }
        return prestatsDataReport;
    }
}