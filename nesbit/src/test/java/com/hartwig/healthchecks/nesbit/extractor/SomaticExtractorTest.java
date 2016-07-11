package com.hartwig.healthchecks.nesbit.extractor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HeaderNotFoundException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.reader.Reader;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

import mockit.Expectations;
import mockit.Mocked;

public class SomaticExtractorTest {

    private static final String DATA_LINE = "2\t212812060\trs839540\tT\tC\t.\tPASS\t" + "AC=1;AF=0.333;AN=3;"
                    + "ANN=C|sequence_feature|LOW|ERBB4|ENSG00000178568|topological_domain:Extracellular|"
                    + "ENST00000342788|protein_coding||c.421+95A>G||||||,C|sequence_feature|LOW|ERBB4|ENSG00000178568|"
                    + "topological_domain:Extracellular|ENST00000436443|protein_coding||c.421+95A>G||||||,C|"
                    + "intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000342788|protein_coding|3/27|"
                    + "c.421+95A>G||||||,C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000436443|"
                    + "protein_coding|3/26|c.421+95A>G||||||,C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|"
                    + "transcript|ENST00000402597|protein_coding|3/27|c.421+95A>G||||||,C|intron_variant|"
                    + "MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000484594|retained_intron|3/19|n.473+95A>G||||||,"
                    + "C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000260943|protein_coding|"
                    + "3/18|c.418+95A>G||||||WARNING_TRANSCRIPT_INCOMPLETE,C|intron_variant|MODIFIER|ERBB4|"
                    + "ENSG00000178568|transcript|ENST00000484474|processed_transcript|2/4|n.338+95A>G||||||,"
                    + "C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000435846|protein_coding|"
                    + "3/4|c.244+95A>G||||||WARNING_TRANSCRIPT_INCOMPLETE;DB;SOMATIC;VT=SNP;set=mutect"
                    + "\tGT:AD:BQ:DP:FA:SS\t./.\t0:25,0:.:25:0.00:0\t./.\t0/1:5,13:40:18:0.722:2\t./. ./. ./. ./.";

    private static final String HEADER_LINE = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\t"
                    + "CPCT12345678R.freebayes\tCPCT12345678R.mutect\tCPCT12345678T.freebayes\tCPCT12345678T.mutect\t"
                    + "NORMAL.strelka\tNORMAL.varscan\tTUMOR.strelka\tTUMOR.varscan";

    private static final String HEADER_NOT_RIGHT = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\t"
                    + "CPCT12345678R.freebayes\tCPCT12345678R.mutect\tCPCT12345678T.mutect\t"
                    + "NORMAL.strelka\tNORMAL.varscan\tTUMOR.strelka\tTUMOR.varscan";

    private static final String FILLING_LINE = "##FILTER=<ID=str10,Description="
                    + "\"Less than 10% or more than 90% of variant supporting reads on one strand\">";

    private static final String TEST_DIR = "Test";

    private static final String MEDIAN_INS_SZ_R = "409";

    private static final String WIDTH_OF_70_PER_R = "247";

    private List<String> lines;

    private List<String> emptyLines;

    private List<String> missingLines;

    private List<String> missingHeaderValue;

    @Mocked
    private Reader reader;

    @Before
    public void setUp() {
        final String dataLine = String.format(DATA_LINE, MEDIAN_INS_SZ_R, WIDTH_OF_70_PER_R);
        lines = Arrays.asList(FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, HEADER_LINE, dataLine,
                        FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE);

        emptyLines = new ArrayList<>();
        missingLines = Arrays.asList(FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, dataLine,
                        FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE);

        missingHeaderValue = Arrays.asList(FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE,
                        HEADER_NOT_RIGHT, dataLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE);
    }

    @Test
    public void extractData() throws IOException, HealthChecksException {

        final SomaticExtractor extractor = new SomaticExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString);
                returns(lines);
            }
        };
        final BaseReport report = extractor.extractFromRunDirectory(TEST_DIR);
        assertEquals("Report with wrong type", CheckType.SOMATIC, report.getCheckType());
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataFromEmptyFile() throws IOException, HealthChecksException {
        final SomaticExtractor extractor = new SomaticExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString);
                returns(emptyLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = IOException.class)
    public void extractDataFromFileIoException() throws IOException, HealthChecksException {
        final SomaticExtractor extractor = new SomaticExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString);
                result = new IOException();
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataMissingHeader() throws IOException, HealthChecksException {
        final SomaticExtractor extractor = new SomaticExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString);
                returns(missingLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = HeaderNotFoundException.class)
    public void extractMissingAHeaderException() throws IOException, HealthChecksException {
        final SomaticExtractor extractor = new SomaticExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString);
                returns(missingHeaderValue);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }
}
