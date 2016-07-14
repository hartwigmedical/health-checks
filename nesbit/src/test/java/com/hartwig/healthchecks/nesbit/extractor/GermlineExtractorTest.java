package com.hartwig.healthchecks.nesbit.extractor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

import com.hartwig.healthchecks.common.exception.HeaderNotFoundException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.reader.FilteredReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.PatientMultiChecksReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

import mockit.Expectations;
import mockit.Mocked;

public class GermlineExtractorTest {

    private static final String PASS_DATA_LINE = "2\t212812060\trs839540\t%s\t%s\t.\tPASS\tAC=1;AF=0.333;AN=3;"
                    + "ANN=C|sequence_feature|LOW|ERBB4|ENSG00000178568|topological_domain:Extracellular|"
                    + "ENST00000342788|protein_coding||c.421+95A>G||||||,C|sequence_feature|LOW|ERBB4|ENSG00000178568|"
                    + "topological_domain:Extracellular|ENST00000436443|protein_coding||c.421+95A>G||||||,C|"
                    + "intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000342788|protein_coding|3/27|"
                    + "c.421+95A>G||||||,C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000436443|"
                    + "protein_coding|3/26|c.421+95A>G||||||,C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|"
                    + "transcript|ENST00000402597|protein_coding|3/27|c.421+95A>G||||||,C|intron_variant|"
                    + "MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000484594|"
                    + "retained_intron|3/19|n.473+95A>G||||||,"
                    + "C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000260943|protein_coding|"
                    + "3/18|c.418+95A>G||||||WARNING_TRANSCRIPT_INCOMPLETE,C|intron_variant|MODIFIER|ERBB4|"
                    + "ENSG00000178568|transcript|ENST00000484474|processed_transcript|2/4|n.338+95A>G||||||,"
                    + "C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000435846|protein_coding|"
                    + "3/4|c.244+95A>G||||||WARNING_TRANSCRIPT_INCOMPLETE;DB;SOMATIC;VT=SNP;set=mutect"
                    + "\tGT:AD:BQ:DP:FA:SS\t./.\t0:25,0:.:25:0.00:0\t./.\t0/1:5,13:40:18:0.722:2\t./. ./. ./. ./.";

    private static final String DOT_DATA_LINE = "2\t212812060\trs839540\t%s\t%s\t.\t.\t" + "AC=1;AF=0.333;AN=3;"
                    + "ANN=C|sequence_feature|LOW|ERBB4|ENSG00000178568|topological_domain:Extracellular|"
                    + "ENST00000342788|protein_coding||c.421+95A>G||||||,C|sequence_feature|LOW|ERBB4|ENSG00000178568|"
                    + "topological_domain:Extracellular|ENST00000436443|protein_coding||c.421+95A>G||||||,C|"
                    + "intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000342788|protein_coding|3/27|"
                    + "c.421+95A>G||||||,C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000436443|"
                    + "protein_coding|3/26|c.421+95A>G||||||,C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|"
                    + "transcript|ENST00000402597|protein_coding|3/27|c.421+95A>G||||||,C|intron_variant|"
                    + "MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000484594|"
                    + "retained_intron|3/19|n.473+95A>G||||||,"
                    + "C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000260943|protein_coding|"
                    + "3/18|c.418+95A>G||||||WARNING_TRANSCRIPT_INCOMPLETE,C|intron_variant|MODIFIER|ERBB4|"
                    + "ENSG00000178568|transcript|ENST00000484474|processed_transcript|2/4|n.338+95A>G||||||,"
                    + "C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000435846|protein_coding|"
                    + "3/4|c.244+95A>G||||||WARNING_TRANSCRIPT_INCOMPLETE;DB;SOMATIC;VT=SNP;set=mutect"
                    + "\tGT:AD:BQ:DP:FA:SS\t./.\t0:25,0:.:25:0.00:0\t./.\t0/1:5,13:40:18:0.722:2\t./. ./. ./. ./.";

    private static final String OTHER_DATA_LINE = "2\t212812060\trs839540\tT\tC\t.\tbla\t" + "AC=1;AF=0.333;AN=3;"
                    + "ANN=C|sequence_feature|LOW|ERBB4|ENSG00000178568|topological_domain:Extracellular|"
                    + "ENST00000342788|protein_coding||c.421+95A>G||||||,C|sequence_feature|LOW|ERBB4|ENSG00000178568|"
                    + "topological_domain:Extracellular|ENST00000436443|protein_coding||c.421+95A>G||||||,C|"
                    + "intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000342788|protein_coding|3/27|"
                    + "c.421+95A>G||||||,C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000436443|"
                    + "protein_coding|3/26|c.421+95A>G||||||,C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|"
                    + "transcript|ENST00000402597|protein_coding|3/27|c.421+95A>G||||||,C|intron_variant|"
                    + "MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000484594|"
                    + "retained_intron|3/19|n.473+95A>G||||||,"
                    + "C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000260943|protein_coding|"
                    + "3/18|c.418+95A>G||||||WARNING_TRANSCRIPT_INCOMPLETE,C|intron_variant|MODIFIER|ERBB4|"
                    + "ENSG00000178568|transcript|ENST00000484474|processed_transcript|2/4|n.338+95A>G||||||,"
                    + "C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000435846|protein_coding|"
                    + "3/4|c.244+95A>G||||||WARNING_TRANSCRIPT_INCOMPLETE;DB;SOMATIC;VT=SNP;set=mutect"
                    + "\tGT:AD:BQ:DP:FA:SS\t./.\t0:25,0:.:25:0.00:0\t./.\t0/1:5,13:40:18:0.722:2\t./. ./. ./. ./.";

    private static final String HEADER_LINE = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT"
                    + "\tCPCT12345678R\tCPCT12345678T";

    private static final String HEADER_NOT_RIGHT = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT"
                    + "\tCPCT12345678R";

    private static final String REF_VALUE = "CT";

    private static final String ALT_VALUE = "CTTTCTTT";

    private static final String TEST_DIR = "Test";

    private static final String GERMLINE_INDELS = "VARIANTS_GERMLINE_INDELS";

    private static final String GERMLINE_SNP = "VARIANTS_GERMLINE_SNP";

    private List<String> dataLines;

    private List<String> headerLines;

    private List<String> missingHeaderValue;

    @Mocked
    private FilteredReader reader;

    @Before
    public void setUp() {
        final String oneDataLine = String.format(PASS_DATA_LINE, REF_VALUE, REF_VALUE);
        final String twoDataLine = String.format(DOT_DATA_LINE, REF_VALUE, ALT_VALUE + "," + ALT_VALUE);
        final String threeDataLine = String.format(PASS_DATA_LINE, REF_VALUE, ALT_VALUE + "," + ALT_VALUE);
        final String fourDataLine = String.format(DOT_DATA_LINE, REF_VALUE, REF_VALUE);
        dataLines = Arrays.asList(oneDataLine, twoDataLine, threeDataLine, fourDataLine, oneDataLine, twoDataLine,
                        threeDataLine, fourDataLine);
        headerLines = Arrays.asList(HEADER_LINE);
        missingHeaderValue = Arrays.asList(HEADER_NOT_RIGHT);
    }

    @Test
    public void extractData() throws IOException, HealthChecksException {

        final GermlineExtractor extractor = new GermlineExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString, (Predicate<String>) any);
                returns(headerLines, dataLines);
            }
        };
        final BaseReport report = extractor.extractFromRunDirectory(TEST_DIR);
        assertEquals("Report with wrong type", CheckType.GERMLINE, report.getCheckType());
        final List<BaseDataReport> patientData = ((PatientMultiChecksReport) report).getPatientData();
        assertEquals("Wrong number of checks", 2, patientData.size());
        final String indels = patientData.stream().filter(data -> data.getCheckName().equals(GERMLINE_INDELS))
                        .findFirst().get().getValue();
        assertEquals("Indels value", "4", indels);
        final String snp = patientData.stream().filter(data -> data.getCheckName().equals(GERMLINE_SNP)).findFirst()
                        .get().getValue();
        assertEquals("snp value", "4", snp);
    }

    @Test(expected = IOException.class)
    public void extractDataFromFileIoException() throws IOException, HealthChecksException {
        final GermlineExtractor extractor = new GermlineExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString, (Predicate<String>) any);
                result = new IOException();
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataMissingHeader() throws IOException, HealthChecksException {
        final GermlineExtractor extractor = new GermlineExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString, (Predicate<String>) any);
                result = new LineNotFoundException("");
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = HeaderNotFoundException.class)
    public void extractMissingAHeaderException() throws IOException, HealthChecksException {
        final GermlineExtractor extractor = new GermlineExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString, (Predicate<String>) any);
                returns(missingHeaderValue);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }
}
