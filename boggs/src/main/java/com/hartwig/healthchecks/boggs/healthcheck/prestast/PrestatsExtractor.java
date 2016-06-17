package com.hartwig.healthchecks.boggs.healthcheck.prestast;

import com.hartwig.healthchecks.boggs.extractor.BoggsExtractor;
import com.hartwig.healthchecks.boggs.model.report.PrestatsDataReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.CheckType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

public class PrestatsExtractor extends BoggsExtractor {
    private static final Logger LOGGER = LogManager.getLogger(PrestatsExtractor.class);

    private static final String DATA_FILE_NAME = "summary.txt";
    private static final String EMPTY_FILES_ERROR = "Found empty Summary files and/or fastqc_data under path -> %s";
    private static final long MIN_TOTAL_SQ = 85000000l;

    public PrestatsReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, EmptyFileException {
        List<PrestatsDataReport> summaryData = getSummaryFilesData(runDirectory);
        PrestatsDataReport fastqcData = getfastqFilesData(runDirectory);

        if (summaryData.isEmpty() || fastqcData == null) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, runDirectory));
        }

        final PrestatsReport prestatsData = new PrestatsReport(CheckType.PRESTATS);
        prestatsData.addAllData(summaryData);
        prestatsData.addData(fastqcData);

        return prestatsData;
    }

    private List<PrestatsDataReport> getSummaryFilesData(@NotNull final String runDirectory) throws IOException {
        final List<Path> summaryFiles = Files.walk(new File(runDirectory).toPath())
                .filter(p -> p.getFileName().toString().startsWith(DATA_FILE_NAME)).sorted()
                .collect(toCollection(ArrayList<Path>::new));

        return summaryFiles.stream().map(path -> {
            Stream<String> fileLines = Stream.empty();
            try {
                fileLines = Files.lines(path);
            } catch (IOException e) {
                LOGGER.error(String.format("Error occurred when reading file. Will return empty stream. Error -> %s",
                        e.getMessage()));
            }
            return fileLines.collect(toList());
        }).flatMap(Collection::stream).map(line -> {
            String[] values = line.split(SEPERATOR_REGEX);
            String status = values[0];
            String check = values[1];
            String file = values[2];
            return new PrestatsDataReport(status, check, file);
        }).collect(Collectors.toList());
    }

    private PrestatsDataReport getfastqFilesData(@NotNull final String runDirectory)
            throws IOException, EmptyFileException {
        final Long totalSequences = sumOfTotalSequences(runDirectory);
        PrestatsDataReport prestatsDataReport = null;
        if (totalSequences != null) {
            String status = "PASS";
            if (totalSequences < MIN_TOTAL_SQ) {
                status = "FAIL";
            }
            prestatsDataReport = new PrestatsDataReport(status, TOTAL_SEQUENCES, "ForNowEmptyFileName");
        }
        return prestatsDataReport;
    }
}