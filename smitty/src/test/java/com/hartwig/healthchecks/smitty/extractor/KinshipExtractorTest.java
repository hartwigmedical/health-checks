package com.hartwig.healthchecks.smitty.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.smitty.reader.KinshipReader;
import com.hartwig.healthchecks.smitty.report.KinshipReport;

import mockit.Expectations;
import mockit.Mocked;

public class KinshipExtractorTest {

    private static final String PATIENT_ID = "CPCT12345678T";

    private static final String EXPECTED_VALUE = "FAIL";

    private static final String WRONG_LIST_SIZE = "Wrong list size";

    private static final String SHOULD_NOT_BE_NULL = "Should Not be Null";

    private List<String> lines;

    private List<String> emptyLines;

    @Mocked
    private KinshipReader kinshipReader;

    @Before
    public void setUp() {

        lines = new ArrayList<>();
        lines.add("FID1\tID1\tFID2\tID2\tN_SNP\tHetHet\tIBS0\tKinship");
        lines.add("CPCT12345678R\tCPCT12345678R\tCPCT12345678T\tCPCT12345678T\t164\t0.274\t0.0122\t0.2705");
        lines.add("CPCT12345678R\tCPCT12345678R\tCPCT12345678TII\tCPCT12345678TII\t156\t0.231\t0.0128\t0.2155");
        lines.add("CPCT12345678T\tCPCT12345678T\tCPCT12345678TII\tCPCT12345678TII\t157\t0.146\t0.0573\t-0.0042");
        emptyLines = new ArrayList<>();
    }

    @Test
    public void extractDataFromKinship() throws IOException, EmptyFileException {

        final KinshipExtractor kinshipExtractor = new KinshipExtractor(kinshipReader);
        new Expectations() {

            {
                kinshipReader.readLinesFromKinship(anyString);
                returns(lines);

            }
        };
        final KinshipReport kinshipReport = kinshipExtractor.extractFromRunDirectory("Test");
        assertNotNull(SHOULD_NOT_BE_NULL, kinshipReport);
        assertEquals(WRONG_LIST_SIZE, 3, kinshipReport.getKnishipData().size());
        assertKinshipData(kinshipReport, PATIENT_ID, EXPECTED_VALUE);

    }

    @Test(expected = EmptyFileException.class)
    public void extractDataFromEmptyKinship() throws IOException, EmptyFileException {
        final KinshipExtractor kinshipExtractor = new KinshipExtractor(kinshipReader);
        new Expectations() {

            {
                kinshipReader.readLinesFromKinship(anyString);
                returns(emptyLines);
            }
        };
        kinshipExtractor.extractFromRunDirectory("Test");
    }

    @Test(expected = IOException.class)
    public void extractDataFromKinshipIoException() throws IOException, EmptyFileException {
        final KinshipExtractor kinshipExtractor = new KinshipExtractor(kinshipReader);
        new Expectations() {

            {
                kinshipReader.readLinesFromKinship(anyString);
                result = new IOException();
            }
        };
        kinshipExtractor.extractFromRunDirectory("Test");
    }

    private void assertKinshipData(final KinshipReport kinshipReport, final String patientId,
                    final String expectedValue) {
        final BaseDataReport baseDataReport = kinshipReport.getKnishipData().stream()
                        .filter(baseData -> baseData.getPatientId().equals(patientId)).findFirst().get();
        assertEquals("Wrong Data", expectedValue, baseDataReport.getValue());
    }
}
