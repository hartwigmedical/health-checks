package com.hartwig.healthchecks.common.util;

import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class ReportTest {

    private Report report = JsonReport.getInstance();

    @Mocked
    private Report jsonReport;

    @Test
    public void generateReport() {
        BaseConfig baseConfig1 = new BaseConfig(CheckType.MAPPING);
        report.addReportData(baseConfig1);

        BaseConfig baseConfig2 = new BaseConfig(CheckType.PRESTATS);
        report.addReportData(baseConfig2);

        Optional<String> location = report.generateReport();

        Assert.assertNotNull(location);
        Assert.assertTrue(location.isPresent());
    }

    @Test
    public void generateReportException() {
        BaseConfig baseConfig1 = new BaseConfig(CheckType.MAPPING);
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