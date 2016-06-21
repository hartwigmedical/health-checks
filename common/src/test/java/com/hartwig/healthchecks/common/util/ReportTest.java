package com.hartwig.healthchecks.common.util;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;

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

        final Optional<String> location = report.generateReport();

        Assert.assertNotNull(location);
        Assert.assertTrue(location.isPresent());
    }

    @Test
    public void generateReportException() {
        final BaseReport baseConfig1 = new BaseReport(CheckType.MAPPING);
        jsonReport.addReportData(baseConfig1);

        new Expectations() {
            {
                jsonReport.generateReport();
                returns(Optional.empty());
            }
        };

        final Optional<String> location = jsonReport.generateReport();

        Assert.assertNotNull(location);
        Assert.assertFalse(location.isPresent());
    }
}