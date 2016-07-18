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
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.reader.FilteredReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.PatientMultiChecksReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

import mockit.Expectations;
import mockit.Mocked;

public class SomaticExtractorTest {

    private static final String INDELS = "INDELS";

    private static final String SNP = "SNP";

    private static final String PROPORTION_CHECK_LABEL = "SOMATIC_%s_PROPORTION_VARIANTS_%s_CALLERS";

    private static final String SENSITIVITY_CHECK_LABEL = "SOMATIC_%s_SENSITIVITY_%s_VARIANTS_2+_CALLERS";

    private static final String PRECISION_CHECK_LABEL = "SOMATIC_%s_PRECISION_%s_2+_CALLERS";

    private static final String WRONG_VALUE_FOR_CHECK = "Wrong Value for check %s";

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

    private static final String HEADER_LINE = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tCPCT12345678T";

    private static final String HEADER_NOT_RIGHT = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT";

    private static final String TEST_DIR = "Test";

    private static final String FREEBAYES = "freebayes";

    private static final String MUTECT = "mutect";

    private static final String STRELKA = "strelka";

    private static final String VARSCAN = "varscan";

    private static final String ALL_SET = "strelka-varscan-freebayes-mutect";

    private static final String ALL_SET_FILTER = "strelka-filterInvarscan-freebayes-mutect";

    private static final String REF_VALUE = "CT";

    private static final String ALT_VALUE = "CTTTCTTT";

    private static final String SOMATIC_INDELS = "VARIANTS_SOMATIC_INDELS";

    private static final String SOMATIC_SNP = "VARIANTS_SOMATIC_SNP";

    private List<String> dataLines;

    private List<String> headerLines;

    private List<String> missingHeaderLines;

    @Mocked
    private FilteredReader reader;

    @Before
    public void setUp() {
        final String oneDataLine = String.format(PASS_DATA_LINE, REF_VALUE, REF_VALUE, FREEBAYES);
        final String twoDataLine = String.format(DOT_DATA_LINE, REF_VALUE, ALT_VALUE + "," + ALT_VALUE, MUTECT);
        final String threeDataLine = String.format(PASS_DATA_LINE, REF_VALUE, ALT_VALUE + "," + ALT_VALUE, STRELKA);
        final String fourDataLine = String.format(DOT_DATA_LINE, REF_VALUE, REF_VALUE, VARSCAN);
        final String fithDataLine = String.format(PASS_DATA_LINE, REF_VALUE, REF_VALUE, ALL_SET);
        final String sixthDataLine = String.format(DOT_DATA_LINE, REF_VALUE, REF_VALUE, ALL_SET_FILTER);

        dataLines = Arrays.asList(oneDataLine, twoDataLine, threeDataLine, fourDataLine, fithDataLine, sixthDataLine,
                        oneDataLine, twoDataLine, threeDataLine, fourDataLine, fithDataLine, sixthDataLine);

        headerLines = Arrays.asList(HEADER_LINE);
        missingHeaderLines = Arrays.asList(HEADER_NOT_RIGHT);

    }

    @Test
    public void extractData() throws IOException, HealthChecksException {

        final DataExtractor extractor = new SomaticExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString, (Predicate<String>) any);
                returns(headerLines, dataLines);
            }
        };
        final BaseReport report = extractor.extractFromRunDirectory(TEST_DIR);
        assertEquals("Report with wrong type", CheckType.SOMATIC, report.getCheckType());
        final List<BaseDataReport> patientData = ((PatientMultiChecksReport) report).getPatientData();
        assertEquals("Wrong number of checks", 26, patientData.size());
        assertPatientReport(patientData, SOMATIC_INDELS, "4");
        assertPatientReport(patientData, SOMATIC_SNP, "8");
        assertPatientReport(patientData, String.format(SENSITIVITY_CHECK_LABEL, SNP, MUTECT.toUpperCase()), "1.0");
        assertPatientReport(patientData, String.format(SENSITIVITY_CHECK_LABEL, INDELS, MUTECT.toUpperCase()), "0.0");
        assertPatientReport(patientData, String.format(PRECISION_CHECK_LABEL, SNP, FREEBAYES.toUpperCase()),
                        "0.6666666666666666");
        assertPatientReport(patientData, String.format(PRECISION_CHECK_LABEL, INDELS, FREEBAYES.toUpperCase()), "0.0");
        assertPatientReport(patientData, String.format(PROPORTION_CHECK_LABEL, SNP, "2"), "0.0");
        assertPatientReport(patientData, String.format(PROPORTION_CHECK_LABEL, INDELS, "2"), "0.0");

        assertPatientReport(patientData, String.format(PROPORTION_CHECK_LABEL, SNP, "4"), "0.0");

    }

    @Test(expected = IOException.class)
    public void extractDataFromFileIoException() throws IOException, HealthChecksException {
        final SomaticExtractor extractor = new SomaticExtractor(reader);
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
        final SomaticExtractor extractor = new SomaticExtractor(reader);
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
        final SomaticExtractor extractor = new SomaticExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString, (Predicate<String>) any);
                returns(missingHeaderLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    private void assertPatientReport(final List<BaseDataReport> patientData, final String checkName,
                    final String expectedValue) {
        final String check = patientData.stream().filter(data -> data.getCheckName().equals(checkName)).findFirst()
                        .get().getValue();
        assertEquals(String.format(WRONG_VALUE_FOR_CHECK, checkName), expectedValue, check);
    }
}
