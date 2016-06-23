package com.hartwig.healthchecks.boggs.healthcheck.reader;

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
        return Arrays.asList(("##FastQC\t0.11.4, >>Basic Statistics\tpass," + " #Measure  Value, "
                        + "Filename\tCPCT12345678R_FLOWCELL_S2_L001_R2_001_fastqc.gz, "
                        + "File type\tConventional base calls, Encoding\tSanger / Illumina 1.9, "
                        + "Total Sequences\t8951, " + "Sequences flagged as poor quality\t0, " + "Sequence length\t151,"
                        + " %GC\t45, >>END_MODULE, " + ">>Per base sequence quality\tpass, "
                        + "#Base Mean\tMedian  Lower Quartile  Upper Quartile  10th Percentile 90th Percentile,"
                        + " 1  31.22609472743521   32.0    32.0    32.0    32.0    32.0, "
                        + "2 31.617292225201073  32.0    32.0    32.0    32.0    32.0").split(","));
    };
}
