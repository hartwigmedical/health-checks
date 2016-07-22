package com.hartwig.healthchecks.boggs.healthcheck.extractor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import com.hartwig.healthchecks.boggs.extractor.MappingCheck;
import com.hartwig.healthchecks.boggs.extractor.MappingExtractor;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.ErrorReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import mockit.Expectations;
import mockit.Mocked;

public class MappingHealthCheckerTest {

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    private static final String DUMMY_ERROR = "DUMMY_ERROR";

    private static final String WRONG_ERROR_MESSAGE = "Wrong Error Message";

    private static final String WRONG_ERROR = "Wrong Error";

    private static final String WRONG_TYPE_MSG = "Report with wrong type";

    @Mocked
    private MappingExtractor dataExtractor;

    @Test
    public void verifyMappingHealthChecker() throws IOException, HealthChecksException {

        new Expectations() {

            {
                dataExtractor.extractFromRunDirectory(anyString);
                returns(dummyData());
            }
        };

        final HealthChecker checker = new HealthCheckerImpl(CheckType.MAPPING, DUMMY_RUN_DIR, dataExtractor);
        final BaseReport report = checker.runCheck();

        assertEquals("Report with wrong type", CheckType.MAPPING, report.getCheckType());
        assertEquals("Dummy", ((SampleReport) report).getReferenceSample().get(0).getPatientId());
        assertEquals(MappingCheck.MAPPING_MAPPED.getDescription(),
                        ((SampleReport) report).getReferenceSample().get(0).getCheckName());
    }

    @Test
    public void verifyMappingHealthCheckerIOException() throws IOException, HealthChecksException {
        new Expectations() {

            {
                dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
                result = new IOException(DUMMY_ERROR);
            }
        };

        final HealthChecker checker = new HealthCheckerImpl(CheckType.MAPPING, DUMMY_RUN_DIR, dataExtractor);
        final BaseReport report = checker.runCheck();
        assertEquals(WRONG_TYPE_MSG, CheckType.MAPPING, report.getCheckType());
        final String error = ((ErrorReport) report).getError();
        final String errorMessage = ((ErrorReport) report).getMessage();

        assertEquals(WRONG_ERROR, IOException.class.getName(), error);
        assertEquals(WRONG_ERROR_MESSAGE, DUMMY_ERROR, errorMessage);
    }

    @Test
    public void verifyMappingHealthCheckerEmptyFileException() throws IOException, HealthChecksException {
        new Expectations() {

            {
                dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
                result = new EmptyFileException(DUMMY_ERROR, "DUMMYPATH");
            }
        };

        final HealthChecker checker = new HealthCheckerImpl(CheckType.MAPPING, DUMMY_RUN_DIR, dataExtractor);
        final BaseReport report = checker.runCheck();
        assertEquals(WRONG_TYPE_MSG, CheckType.MAPPING, report.getCheckType());
        final String error = ((ErrorReport) report).getError();
        final String errorMessage = ((ErrorReport) report).getMessage();

        assertEquals(WRONG_ERROR, EmptyFileException.class.getName(), error);
        assertEquals(WRONG_ERROR_MESSAGE, "File DUMMY_ERROR was found empty in path -> DUMMYPATH", errorMessage);
    }

    @NotNull
    private static SampleReport dummyData() {
        final List<BaseDataReport> reports = createDummyBaseData();
        return new SampleReport(CheckType.MAPPING, reports, reports);
    }

    @NotNull
    private static List<BaseDataReport> createDummyBaseData() {
        final BaseDataReport mappedData = new BaseDataReport("Dummy", MappingCheck.MAPPING_MAPPED.getDescription(),
                        "0.0");
        final BaseDataReport duplicateData = new BaseDataReport("Dummy",
                        MappingCheck.MAPPING_DUPLIVATES.getDescription(), "0.0");
        final BaseDataReport properlyData = new BaseDataReport("Dummy",
                        MappingCheck.MAPPING_PROPERLY_PAIRED.getDescription(), "0.0");

        final List<BaseDataReport> reports = Arrays.asList(mappedData, duplicateData, properlyData);
        return reports;
    }
}
