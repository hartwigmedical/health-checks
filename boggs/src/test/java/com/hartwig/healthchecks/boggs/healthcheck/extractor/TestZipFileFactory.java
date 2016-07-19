package com.hartwig.healthchecks.boggs.healthcheck.extractor;

import java.util.Arrays;
import java.util.List;

public class TestZipFileFactory {

    public static List<String> getSummaryLines(final String lane, final String suffix, final String perTSQStatus,
                    final String seqLengthStatus, final String duplicationLevelStatus) {
        final String[] lines = ("PASS\tBasic Statistics\t" + "CPCT12345678" + suffix + "FLOWCELL_S2_" + lane
                        + "_001.fastq.gz, " + "PASS\tPer base sequence quality\t" + "CPCT12345678" + suffix
                        + "FLOWCELL_S2_" + lane + "_001.fastq.gz, " + perTSQStatus + "\tPer tile sequence quality\t"
                        + "CPCT12345678" + suffix + "FLOWCELL_S2_" + lane + "_001.fastq.gz, "
                        + "PASS\tPer sequence quality scores\t" + "CPCT12345678" + suffix + "FLOWCELL_S2_" + lane
                        + "_001.fastq.gz, " + "PASS\tPer base sequence content\t" + "CPCT12345678" + suffix
                        + "FLOWCELL_S2_" + lane + "_001.fastq.gz, " + "PASS\tPer sequence GC content\t" + "CPCT12345678"
                        + suffix + "FLOWCELL_S2_" + lane + "_001.fastq.gz, " + "PASS\tPer base N content\t"
                        + "CPCT12345678" + suffix + "FLOWCELL_S2_" + lane + "_001.fastq.gz, " + seqLengthStatus
                        + "\tSequence Length Distribution\t" + "CPCT12345678" + suffix + "FLOWCELL_S2_" + lane
                        + "_001.fastq.gz, " + duplicationLevelStatus + "\tSequence Duplication Levels\t"
                        + "CPCT12345678" + suffix + "FLOWCELL_S2_" + lane + "_001.fastq.gz, "
                        + "WARN\tOverrepresented sequences\t" + "CPCT12345678" + suffix + "FLOWCELL_S2_" + lane
                        + "_001.fastq.gz, " + "PASS\tAdapter Content\tCPCT12345678" + suffix + "FLOWCELL_S2_" + lane
                        + "_001.fastq.gz, " + "WARN\tKmer Content\tCPCT12345678" + suffix + "FLOWCELL_S2_" + lane
                        + "_001.fastq.gz," + "WARN\tUNKNOWN\tCPCT12345678" + suffix + "FLOWCELL_S2_" + lane
                        + "_001.fastq.gz").split(",");
        return Arrays.asList(lines);
    };

    public static List<String> getFastqLines() {
        return Arrays.asList("Total Sequences\t8951");
    };
}
