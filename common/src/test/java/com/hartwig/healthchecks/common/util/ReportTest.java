package com.hartwig.healthchecks.common.util;

import org.junit.Test;

public class ReportTest {

    Report report = Report.getInstance();

    @Test
    public void generateReport() throws Exception {
        BaseConfig baseConfig1 = new BaseConfig(CheckType.MAPPING);
        report.addReportData(baseConfig1);

        BaseConfig baseConfig2 = new BaseConfig(CheckType.PRESTATS);
        report.addReportData(baseConfig2);

        report.generateReport();
    }
}