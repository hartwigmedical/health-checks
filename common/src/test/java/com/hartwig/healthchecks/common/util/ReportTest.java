package com.hartwig.healthchecks.common.util;

import java.util.Optional;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.GenerateReportException;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;

import org.junit.Assert;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;

public class ReportTest {

    private final Report report = JsonReport.getInstance();

    @Mocked
    private Report jsonReport;

    @Test
    public void generateReport() {
        final BaseReport baseConfig1 = new BaseReport(CheckType.MAPPING);
        report.addReportData(baseConfig1);

        final BaseReport baseConfig2 = new BaseReport(CheckType.PRESTATS);
        report.addReportData(baseConfig2);

        try {
            final Optional<String> location = report.generateReport();

            Assert.assertNotNull(location);
            Assert.assertTrue(location.isPresent());
        } catch (GenerateReportException e) {
            Assert.fail("Failed to generate report.");
        }
    }

    @Test(expected = GenerateReportException.class)
    public void generateReportException() throws GenerateReportException {
        final BaseReport baseConfig1 = new BaseReport(CheckType.MAPPING);
        jsonReport.addReportData(baseConfig1);

        new Expectations() {
            {
                jsonReport.generateReport();
                result = new GenerateReportException("Exception occurred.");
            }
        };

        final Optional<String> location = jsonReport.generateReport();

        Assert.assertNotNull(location);
        Assert.assertFalse(location.isPresent());
    }
}