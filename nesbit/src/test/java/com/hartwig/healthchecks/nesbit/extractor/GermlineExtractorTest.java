package com.hartwig.healthchecks.nesbit.extractor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HeaderNotFoundException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.reader.ExtensionFinderAndLineReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.junit.Before;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;

public class GermlineExtractorTest {

    private static final String PASS_DATA_LINE = "1\t10329\trs150969722\t%s\t%s\t76.71\tPASS\t"
                    + "AC=2;AF=1.00;AN=2;DB;DP=782;FS=0.000;MLEAC=2;MLEAF=1.00;MQ=23.61;QD=25.57;SOR=1.179\t"
                    + "GT:AD:DP:GQ:PL\t%s\t%s";

    private static final String DOT_DATA_LINE = "1\t10329\trs150969722\t%s\t%s\t76.71\t.\t"
                    + "AC=2;AF=1.00;AN=2;DB;DP=782;FS=0.000;MLEAC=2;MLEAF=1.00;MQ=23.61;QD=25.57;SOR=1.179\t"
                    + "GT:AD:DP:GQ:PL\t%s\t%s";

    private static final String HEADER_LINE = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT"
                    + "\tCPCT12345678R\tCPCT12345678T";

    private static final String HEADER_NOT_RIGHT = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT"
                    + "\tCPCT12345678R";

    private static final String NOT_VARIANT = "./.:773,0:773";
    private static final String VARIANT = "1/1:0,3:3:9:103,9,0";
    private static final String REF_VALUE = "CT";
    private static final String ALT_VALUE = "CTTTCTTT";
    private static final String TEST_DIR = "Test";
    private static final String GERMLINE_INDELS = "VARIANTS_GERMLINE_INDELS";
    private static final String GERMLINE_SNP = "VARIANTS_GERMLINE_SNP";

    private List<String> dataLines;
    private List<String> headerLines;
    private List<String> missingHeaderValue;

    @Mocked
    private ExtensionFinderAndLineReader reader;

    @Before
    public void setUp() {
        final String oneDataLine = String.format(PASS_DATA_LINE, REF_VALUE, REF_VALUE, VARIANT, NOT_VARIANT);
        final String twoDataLine = String.format(DOT_DATA_LINE, REF_VALUE, ALT_VALUE + "," + ALT_VALUE, VARIANT,
                        VARIANT);
        final String threeDataLine = String.format(PASS_DATA_LINE, REF_VALUE, ALT_VALUE + "," + ALT_VALUE, VARIANT,
                        NOT_VARIANT);
        final String fourDataLine = String.format(DOT_DATA_LINE, REF_VALUE, REF_VALUE, NOT_VARIANT, VARIANT);
        dataLines = Arrays.asList(oneDataLine, twoDataLine, threeDataLine, fourDataLine, oneDataLine, twoDataLine,
                        threeDataLine, fourDataLine);
        headerLines = Collections.singletonList(HEADER_LINE);
        missingHeaderValue = Collections.singletonList(HEADER_NOT_RIGHT);
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
        final List<BaseDataReport> refData = ((SampleReport) report).getReferenceSample();
        final List<BaseDataReport> tumData = ((SampleReport) report).getTumorSample();

        assertSampleData(refData, "2", "4");
        assertSampleData(tumData, "2", "2");
    }

    private static void assertSampleData(final List<BaseDataReport> sampleData, final String expectedCountSNP,
                    final String expectedCountIndels) {
        assertEquals("Wrong number of checks", 2, sampleData.size());
        final String indels = sampleData.stream().filter(data -> data.getCheckName().equals(GERMLINE_INDELS))
                        .findFirst().get().getValue();
        assertEquals("Indels value", expectedCountIndels, indels);
        final String snp = sampleData.stream().filter(data -> data.getCheckName().equals(GERMLINE_SNP)).findFirst()
                        .get().getValue();
        assertEquals("snp value", expectedCountSNP, snp);
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
                result = new LineNotFoundException("", "");
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
                returns(missingHeaderValue, dataLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }
}
