package com.hartwig.healthchecks.boggs.model.data;

import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatParser;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

public class PatientExtractor {
    private static final Logger LOGGER = LogManager.getLogger(PatientExtractor.class);

    private static final String MAPPING = "mapping";
    private static final String SORTED = "sorted";
    private static final String DEDUP = "dedup";
    private static final String REALIGN = "realign";
    private static final String SAMPLE_PREFIX = "CPCT";
    private static final String REF_SAMPLE_SUFFIX = "R";
    private static final String TUMOR_SAMPLE_SUFFIX = "T";
    private static final String FLAGSTAT_SUFFIX = ".flagstat";

    @NotNull
    private final FlagStatParser flagstatParser;

    public PatientExtractor(@NotNull final FlagStatParser flagstatParser) {
        this.flagstatParser = flagstatParser;
    }

    @NotNull
    public PatientData extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, EmptyFileException {
        SampleData refSample = extractSample(runDirectory, SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);
        SampleData tumorSample = extractSample(runDirectory, SAMPLE_PREFIX, TUMOR_SAMPLE_SUFFIX);
        return new PatientData(refSample, tumorSample);
    }

    @NotNull
    private SampleData extractSample(@NotNull final String runDirectory, @NotNull final String startsWith, @NotNull final String endsWith)
            throws IOException, EmptyFileException {

        final Optional<Path> sampleFile = Files.walk(new File(runDirectory).toPath()).filter(
                p -> p.getFileName().toString().startsWith(startsWith) && p.getFileName().toString().endsWith(endsWith))
                .findFirst();

        assert sampleFile.isPresent();

        final String externalID = sampleFile.get().getFileName().toString();

        final FlagStatData markdupFlagstats = parseFile(sampleFile.get(), DEDUP);
        final FlagStatData realignedFlagstats = parseFile(sampleFile.get(), REALIGN);

        final List<FlagStatData> sortedMappingFlagstats = parseSortedFiles(sampleFile.get(), SORTED);
        final List<FlagStatData> rawMappingFlagstats = parseRestOfFiles(sampleFile.get());

        assert markdupFlagstats != null;
        assert realignedFlagstats != null;

        return new SampleData(externalID, rawMappingFlagstats, sortedMappingFlagstats, markdupFlagstats,
                realignedFlagstats);
    }

    private FlagStatData parseFile(@NotNull final Path sampleFile, @NotNull final String filePartName)
            throws IOException, EmptyFileException {
        final Optional<Path> filePath = Files.walk(new File(sampleFile + File.separator + MAPPING + File.separator).toPath())
                .filter(p -> p.getFileName().toString().endsWith(FLAGSTAT_SUFFIX)
                        && p.getFileName().toString().contains(filePartName))
                .findFirst();
        assert filePath.isPresent();

        return flagstatParser.parse(filePath.get().toString());
    }

    private List<FlagStatData> parseSortedFiles(@NotNull final Path sampleFile, @NotNull final String filePartName) throws IOException {
        final List<Path> filesPaths = Files.walk(new File(sampleFile + File.separator + MAPPING + File.separator).toPath())
                .filter(p -> p.getFileName().toString().endsWith(FLAGSTAT_SUFFIX)
                        && p.getFileName().toString().contains(filePartName))
                .sorted().collect(toCollection(ArrayList<Path>::new));
        return filesPaths.stream().map(path -> {
            FlagStatData parsedFlagstatData = null;
            try {
                parsedFlagstatData = flagstatParser.parse(path.toString());
            } catch (EmptyFileException e) {
                LOGGER.error(String.format("Error occurred when reading file. Will return empty stream. Error -> %s",
                        e.getMessage()));
            } catch (IOException e) {
                LOGGER.error(String.format("Error occurred when reading file. Will return empty stream. Error -> %s",
                        e.getMessage()));
            }
            return parsedFlagstatData;
        }).collect(Collectors.toList());
    }

    private List<FlagStatData> parseRestOfFiles(@NotNull final Path sampleFile) throws IOException {
        final List<Path> filesPaths = Files.walk(new File(sampleFile + File.separator + MAPPING + File.separator).toPath())
                .filter(p -> p.getFileName().toString().endsWith(FLAGSTAT_SUFFIX)
                        && !p.getFileName().toString().contains(REALIGN) && !p.getFileName().toString().contains(DEDUP)
                        && !p.getFileName().toString().contains(SORTED))
                .sorted().collect(toCollection(ArrayList<Path>::new));
        return filesPaths.stream().map(path -> {
            FlagStatData parsedFlagstatData = null;
            try {
                parsedFlagstatData = flagstatParser.parse(path.toString());
            } catch (EmptyFileException e) {
                LOGGER.error(String.format("Error occurred when reading file. Will return empty stream. Error -> %s",
                        e.getMessage()));
            } catch (IOException e) {
                LOGGER.error(String.format("Error occurred when reading file. Will return empty stream. Error -> %s",
                        e.getMessage()));
            }
            return parsedFlagstatData;
        }).collect(Collectors.toList());
    }
}
