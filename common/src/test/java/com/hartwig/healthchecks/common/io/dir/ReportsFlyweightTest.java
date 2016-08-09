package com.hartwig.healthchecks.common.io.dir;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import com.hartwig.healthchecks.common.adapter.ReportsFlyweight;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.report.StandardOutputReport;

import org.junit.Test;

public class ReportsFlyweightTest {

    private static final String BLA = "BLA";
    private static final String STDOUT = "stdout";
    private static final String JSON = "json";

    @Test
    public void getReport() throws IOException, HealthChecksException {
        Report report = ReportsFlyweight.getInstance().getReport(STDOUT);
        assertNotNull("STDOUT Report is null", report);
        assertTrue("WRONG REPORT TYPE", report instanceof StandardOutputReport);

        report = ReportsFlyweight.getInstance().getReport(JSON);
        assertNotNull("JSON Report is null", report);
        assertTrue("WRONG REPORT TYPE", report instanceof JsonReport);

        report = ReportsFlyweight.getInstance().getReport(BLA);
        assertNotNull("JSON Report is null", report);
        assertTrue("WRONG REPORT TYPE", report instanceof StandardOutputReport);
    }
}
