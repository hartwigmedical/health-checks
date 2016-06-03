package com.hartwig.healthchecks.boggs.io;

import com.google.common.collect.Lists;
import com.hartwig.healthchecks.boggs.PatientData;
import com.hartwig.healthchecks.boggs.SampleData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatParser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

public class PatientExtractor {

    private static final String SAMPLE_PREFIX = "CPCT";
    private static final String REF_SAMPLE_SUFFIX = "R";
    private static final String TUMOR_SAMPLE_SUFFIX = "T";

    private static final String FLAGSTAT_SUFFIX = ".flagstat";

    @NotNull
    private final FlagStatParser flagstatParser;

    public PatientExtractor(@NotNull FlagStatParser flagstatParser) {
        this.flagstatParser = flagstatParser;
    }

    @NotNull
    private static FilenameFilter refSampleFilter() {
        return new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(SAMPLE_PREFIX) && name.endsWith(REF_SAMPLE_SUFFIX);
            }
        };
    }

    @NotNull
    private static FilenameFilter tumorSampleFilter() {
        return new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(SAMPLE_PREFIX) && name.endsWith(TUMOR_SAMPLE_SUFFIX);
            }
        };
    }

    @NotNull
    private static FilenameFilter flagstatFilter() {
        return new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(FLAGSTAT_SUFFIX);
            }
        };
    }

    @NotNull
    public PatientData extractFromRunDirectory(@NotNull String runDirectory) throws IOException {
        File directory = new File(runDirectory);

        SampleData refSample = extractSample(directory, refSampleFilter());
        SampleData tumorSample = extractSample(directory, tumorSampleFilter());

        return new PatientData(refSample, tumorSample);
    }

    @NotNull
    private SampleData extractSample(@NotNull File runDirectory, @NotNull FilenameFilter filter)
            throws IOException {
        File[] samples = runDirectory.listFiles(filter);

        assert samples.length == 1;

        String externalID = samples[0].getName();

        File flagstatDir = new File(samples[0].getPath() + File.separator + "mapping" + File.separator);
        File[] flagstats = flagstatDir.listFiles(flagstatFilter());

        List<FlagStatData> rawMappingFlagstats = Lists.newArrayList();
        List<FlagStatData> sortedMappingFlagstats = Lists.newArrayList();
        FlagStatData markdupFlagstats = null;
        FlagStatData realignedFlagstats = null;
        for (File flagstat : flagstats) {
            FlagStatData parsedFlagstatData = flagstatParser.parse(flagstat);
            String name = flagstat.getName();
            if (name.contains("realign")) {
                realignedFlagstats = parsedFlagstatData;
            } else if (name.contains("dedup")) {
                markdupFlagstats = parsedFlagstatData;
            } else if (name.contains("sorted")) {
                sortedMappingFlagstats.add(parsedFlagstatData);
            } else {
                rawMappingFlagstats.add(parsedFlagstatData);
            }
        }

        assert markdupFlagstats != null;
        assert realignedFlagstats != null;

        return new SampleData(externalID, rawMappingFlagstats,
                sortedMappingFlagstats, markdupFlagstats, realignedFlagstats);
    }
}
