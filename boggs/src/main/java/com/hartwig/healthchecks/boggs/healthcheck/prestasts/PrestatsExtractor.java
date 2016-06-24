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

import com.hartwig.healthchecks.boggs.extractor.BoggsExtractor;
import com.hartwig.healthchecks.boggs.model.report.BaseDataReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.CheckType;

import org.jetbrains.annotations.NotNull;

public class PrestatsExtractor extends BoggsExtractor {

    protected static final String PASS = "PASS";

    protected static final String WARN = "WARN";

    protected static final String FAIL = "FAIL";

    private static final int EXPECTED_LINE_LENGTH = 3;

    private static final int ONE = 1;

    private static final int NEGATIVE_ONE = -1;

    private static final int ZERO = 0;

    private static final String SUMMARY_FILE_NAME = "summary.txt";

    private static final long MIN_TOTAL_SQ = 85000000L;

    public PrestatsReport extractFromRunDirectory(@NotNull final String runDirectory)
                    throws IOException, EmptyFileException {
        final Optional<Path> pathToCheck = getFilesPath(runDirectory, SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);

        final String patientId = pathToCheck.get().getFileName().toString();

        final List<BaseDataReport> summaryData = getSummaryFilesData(pathToCheck.get(), patientId);

        final BaseDataReport fastqcData = getFastqFilesData(pathToCheck.get(), patientId);

        if (summaryData == null || summaryData.isEmpty() || fastqcData == null) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, runDirectory));
        }

        final PrestatsReport prestatsData = new PrestatsReport(CheckType.PRESTATS);
        prestatsData.addAllData(summaryData);
        prestatsData.addData(fastqcData);

        return prestatsData;
    }

    private List<BaseDataReport> getSummaryFilesData(@NotNull final Path pathToCheck,
                    @NotNull final String patientId) throws IOException, EmptyFileException {

        final List<Path> zipFiles = Files.walk(pathToCheck)
                .filter(path -> path.getFileName().toString().endsWith(ZIP_FILES_SUFFIX)).sorted()
                .collect(toCollection(ArrayList<Path>::new));

        final Map<String, List<BaseDataReport>> data = zipFiles
                .stream()
                .map(path -> {
                    List<String> lines = null;
                    lines = getLinesFromFile(path, SUMMARY_FILE_NAME);
                    if (lines == null) {
                        lines = new ArrayList<>();
                    }
                    return lines;
                 })
                .flatMap(Collection::stream)
                .map(line -> {
                    final String[] values = line.split(SEPERATOR_REGEX);
                    BaseDataReport prestatsDataReport = null;
                    if (values.length == EXPECTED_LINE_LENGTH) {
                        final String status = values[0];
                        final Optional<PrestatsCheck> check = PrestatsCheck.getByDescription(values[1]);
                        if (check.isPresent()) {
                            prestatsDataReport = new BaseDataReport(patientId, check.get().getDescription(), status);
                        }
                    }
                    return prestatsDataReport;
                })
                .filter(prestatsDataReport -> prestatsDataReport != null)
                .collect(groupingBy(baseDataReport -> {
                    return baseDataReport.getCheckName();
                }));

        return data.values()
                .stream()
                .map(prestatsDataReportList -> {
                    return prestatsDataReportList.stream().min(isStatusWorse()).get();
                })
                .collect(toList());
    }

    private Comparator<BaseDataReport> isStatusWorse() {

        Comparator<BaseDataReport> isStatusWorse = (firstData, secondData) -> {
            final String firstStatus = firstData.getValue();
            final String secondStatus = secondData.getValue();
            int status = ONE;
            if (firstStatus.equals(secondStatus)) {
                status = ZERO;
            } else if (FAIL.equals(firstStatus) || WARN.equals(firstStatus)) {
                status = NEGATIVE_ONE;
            }
            return status;
        };
        return isStatusWorse;
    }

    private BaseDataReport getFastqFilesData(@NotNull final Path pathToCheck, @NotNull final String patientId)
                    throws IOException, EmptyFileException {
        final Long totalSequences = sumOfTotalSequences(pathToCheck);
        BaseDataReport prestatsDataReport = null;
        if (totalSequences != null) {
            String status = PASS;
            if (totalSequences < MIN_TOTAL_SQ) {
                status = FAIL;
            }
            prestatsDataReport = new BaseDataReport(patientId, PrestatsCheck.PRESTATS_NUMBER_OF_READS.toString(), status);
        }
        return prestatsDataReport;
    }
}