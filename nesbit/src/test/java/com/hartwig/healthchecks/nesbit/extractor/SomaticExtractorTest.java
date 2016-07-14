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
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.reader.Reader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.PatientMultiChecksReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

import mockit.Expectations;
import mockit.Mocked;

public class SomaticExtractorTest {

    private static final String DOT_DATA_LINE = "1\t14354\t.\t%s\t%s\t37.46\t.\t"
                    + "AB=0.234375;ABP=42.2325;AC=1;AF=0.250;AN=4;ANN=A|non_coding_exon_variant|MODIFIER|DDX11L1|"
                    + "ENSG00000223972|transcript|ENST00000456328|processed_transcript|3/3|n.1602C>A||||||,A|"
                    + "non_coding_exon_variant|MODIFIER|DDX11L1|ENSG00000223972|transcript|ENST00000515242|"
                    + "transcribed_unprocessed_pseudogene|3/3|n.1595C>A||||||,A|non_coding_exon_variant|"
                    + "MODIFIER|DDX11L1|ENSG00000223972|transcript|ENST00000518655|transcribed_unprocessed_pseudogene|"
                    + "4/4|n.1428C>A||||||;AO=19;CIGAR=1X;DP=95;DPB=95;DPRA=0;EPP=5.8675;"
                    + "EPPR=24.117;FB_SSC=37.4603;GTI=0;LEN=1;MEANALT=1.5;MQM=22.3684;MQMR=22.1733;NS=2;"
                    + "NUMALT=1;ODDS=8.62538;PAIRED=1;PAIREDR=1;PAO=0;PQA=0;PQR=0;PRO=0;QA=574;QR=2694;RO=75;"
                    + "RPP=5.8675;RPPR=3.73412;RUN=1;SAF=15;SAP=16.8392;SAR=4;SRF=42;SRP=5.35549;SRR=33;TYPE=snp;"
                    + "VT=somatic;set=%s;technology.ILLUMINA=1;CSA=1,1;CSP=1\tGT:AD:DP\t0/1:48,15:64";

    private static final String PASS_DATA_LINE = "1\t14354\t.\t%s\t%s\t37.46\tPASS\t"
                    + "AB=0.234375;ABP=42.2325;AC=1;AF=0.250;AN=4;ANN=A|non_coding_exon_variant|MODIFIER|DDX11L1|"
                    + "ENSG00000223972|transcript|ENST00000456328|processed_transcript|3/3|n.1602C>A||||||,A|"
                    + "non_coding_exon_variant|MODIFIER|DDX11L1|ENSG00000223972|transcript|ENST00000515242|"
                    + "transcribed_unprocessed_pseudogene|3/3|n.1595C>A||||||,A|non_coding_exon_variant|"
                    + "MODIFIER|DDX11L1|ENSG00000223972|transcript|ENST00000518655|transcribed_unprocessed_pseudogene|"
                    + "4/4|n.1428C>A||||||;AO=19;CIGAR=1X;DP=95;DPB=95;DPRA=0;EPP=5.8675;"
                    + "EPPR=24.117;FB_SSC=37.4603;GTI=0;LEN=1;MEANALT=1.5;MQM=22.3684;MQMR=22.1733;NS=2;"
                    + "NUMALT=1;ODDS=8.62538;PAIRED=1;PAIREDR=1;PAO=0;PQA=0;PQR=0;PRO=0;QA=574;QR=2694;RO=75;"
                    + "RPP=5.8675;RPPR=3.73412;RUN=1;SAF=15;SAP=16.8392;SAR=4;SRF=42;SRP=5.35549;SRR=33;TYPE=snp;"
                    + "VT=somatic;set=%s;technology.ILLUMINA=1;CSA=1,1;CSP=1\tGT:AD:DP\t0/1:48,15:64";

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

    private static final String HEADER_LINE = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tCPCT12345678T";

    private static final String HEADER_NOT_RIGHT = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT";

    private static final String FILLING_LINE = "##FILTER=<ID=str10,Description="
                    + "\"Less than 10% or more than 90% of variant supporting reads on one strand\">";

    private static final String TEST_DIR = "Test";

    private static final String FREEBAYES = "freebayes";

    private static final String MUTECT = "mutect";

    private static final String STRELKA = "strelka";

    private static final String VARSCAN = "varscan";

    private static final String ALL_SET = "strelka-varscan-freebayes-mutect";

    private static final String ALL_SET_FILTER = "strelka-filterInvarscan-freebayes-mutect";

    private static final String REF_VALUE = "CT";

    private static final String ALT_VALUE = "CTTTCTTT";

    private List<String> lines;

    private List<String> emptyLines;

    private List<String> missingLines;

    private List<String> missingHeaderValue;

    private static final String SOMATIC_INDELS = "VARIANTS_SOMATIC_INDELS";

    private static final String SOMATIC_SNP = "VARIANTS_SOMATIC_SNP";

    @Mocked
    private Reader reader;

    @Before
    public void setUp() {
        final String oneDataLine = String.format(PASS_DATA_LINE, REF_VALUE, REF_VALUE, FREEBAYES);
        final String twoDataLine = String.format(DOT_DATA_LINE, REF_VALUE, ALT_VALUE + "," + ALT_VALUE, MUTECT);
        final String threeDataLine = String.format(PASS_DATA_LINE, REF_VALUE, ALT_VALUE + "," + ALT_VALUE, STRELKA);
        final String fourDataLine = String.format(DOT_DATA_LINE, REF_VALUE, REF_VALUE, VARSCAN);
        final String fithDataLine = String.format(DOT_DATA_LINE, REF_VALUE, REF_VALUE, ALL_SET);
        final String sixthDataLine = String.format(DOT_DATA_LINE, REF_VALUE, REF_VALUE, ALL_SET_FILTER);

        lines = Arrays.asList(FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, HEADER_LINE, oneDataLine,
                        twoDataLine, threeDataLine, fourDataLine, fithDataLine, sixthDataLine, OTHER_DATA_LINE,
                        oneDataLine, twoDataLine, threeDataLine, fourDataLine, fithDataLine, sixthDataLine);

        emptyLines = new ArrayList<>();
        missingLines = Arrays.asList(FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, oneDataLine,
                        twoDataLine, threeDataLine, fourDataLine, fithDataLine, sixthDataLine, OTHER_DATA_LINE);

        missingHeaderValue = Arrays.asList(FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE,
                        HEADER_NOT_RIGHT, oneDataLine, twoDataLine, threeDataLine, fourDataLine, fithDataLine,
                        sixthDataLine, OTHER_DATA_LINE);
    }

    @Test
    public void extractData() throws IOException, HealthChecksException {

        final DataExtractor extractor = new SomaticExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString);
                returns(lines);
            }
        };
        final BaseReport report = extractor.extractFromRunDirectory(TEST_DIR);
        assertEquals("Report with wrong type", CheckType.SOMATIC, report.getCheckType());
        final List<BaseDataReport> patientData = ((PatientMultiChecksReport) report).getPatientData();
        assertEquals("Wrong number of checks", 2, patientData.size());
        final String indels = patientData.stream().filter(data -> data.getCheckName().equals(SOMATIC_INDELS))
                        .findFirst().get().getValue();
        assertEquals("Indels value", "4", indels);
        final String snp = patientData.stream().filter(data -> data.getCheckName().equals(SOMATIC_SNP)).findFirst()
                        .get().getValue();
        assertEquals("snp value", "8", snp);

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
