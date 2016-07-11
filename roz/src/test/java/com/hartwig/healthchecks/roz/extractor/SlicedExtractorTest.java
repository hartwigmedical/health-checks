package com.hartwig.healthchecks.roz.extractor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.reader.Reader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.PatientReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

import mockit.Expectations;
import mockit.Mocked;

public class SlicedExtractorTest {

    private static final String EXPECTED_VALUE = "5";

    private static final String WRONG_NAME = "Wrong name";

    private static final String SLICED_NUM_VARIANTS = "SLICED_NUMBER_OF_VARIANTS";

    private static final String REPORT_WITH_WRONG_TYPE = "Report with wrong type";

    private static final String DATA_LINE = "2\t29940529\trs2246745;COSM4416269\tA\tT\t2627.44\tPASS\t"
                    + "AC=2;AF=0.500;AN=4;ANN=T|synonymous_variant|LOW|ALK|ENSG00000171094|transcript|ENST00000389048|"
                    + "protein_coding|2/29|c.702T>A|p.Pro234Pro|1609/6220|702/4863|234/1620||,T|synonymous_variant|"
                    + "LOW|ALK|ENSG00000171094|transcript|ENST00000431873|protein_coding|2/4|c.702T>A|p.Pro234Pro|"
                    + "702/1353|702/1353|234/450||,T|sequence_feature|LOW|ALK|ENSG00000171094|"
                    + "topological_domain:Extracellular|ENST00000389048|protein_coding||c.702T>A||||||"
                    + ";BaseQRankSum=-1.330e-01;ClippingRankSum=0.996;DB;DP=122;FS=4.615;MLEAC=2;MLEAF=0.500;MQ=60.00;"
                    + "MQRankSum=0.481;QD=21.54;ReadPosRankSum=0.700;SOR=0.470;GoNLv5_AC=802;GoNLv5_AF=0.804;"
                    + "GoNLv5_AN=998\tGT:AD:DP:GQ:PL\t0/1:12,17:29:99:500,0,440\t0/1:32,61:93:99:2156,0,1092";

    private static final String HEADER_LINE = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT"
                    + "\tCPCT12345678R\tCPCT12345678T";

    private static final String FILLING_LINE = "##FILTER=<ID=str10,Description="
                    + "\"Less than 10% or more than 90% of variant supporting reads on one strand\">";

    private static final String TEST_DIR = "Test";

    private List<String> lines;

    private List<String> emptyLines;

    private List<String> missingLines;

    @Mocked
    private Reader reader;

    @Before
    public void setUp() {
        lines = Arrays.asList(FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, HEADER_LINE, DATA_LINE, DATA_LINE,
                        DATA_LINE, DATA_LINE, DATA_LINE);

        emptyLines = new ArrayList<>();
        missingLines = Arrays.asList(FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, DATA_LINE,
                        DATA_LINE, DATA_LINE, DATA_LINE, DATA_LINE);
    }

    @Test
    public void extractData() throws IOException, HealthChecksException {

        final SlicedExtractor extractor = new SlicedExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString);
                returns(lines);
            }
        };
        final BaseReport report = extractor.extractFromRunDirectory(TEST_DIR);
        assertEquals(REPORT_WITH_WRONG_TYPE, CheckType.SLICED, report.getCheckType());
        final BaseDataReport patientData = ((PatientReport) report).getPatientData();
        assertEquals(WRONG_NAME, SLICED_NUM_VARIANTS, patientData.getCheckName());
        assertEquals("Wrong Patient", "CPCT12345678R", patientData.getPatientId());
        assertEquals("Wrong value", EXPECTED_VALUE, patientData.getValue());
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataFromEmptyFile() throws IOException, HealthChecksException {
        final SlicedExtractor extractor = new SlicedExtractor(reader);
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
        final SlicedExtractor extractor = new SlicedExtractor(reader);
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
        final SlicedExtractor extractor = new SlicedExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString);
                returns(missingLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }
}
