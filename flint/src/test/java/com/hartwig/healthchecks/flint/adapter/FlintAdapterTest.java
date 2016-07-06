package com.hartwig.healthchecks.flint.adapter;

import java.util.Arrays;

import org.junit.Test;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.flint.extractor.InsertSizeMetricsExtractor;
import com.hartwig.healthchecks.flint.extractor.SummaryMetricsExtractor;
import com.hartwig.healthchecks.flint.extractor.WGSExtractor;
import com.hartwig.healthchecks.flint.report.InsertSizeMetricsReport;
import com.hartwig.healthchecks.flint.report.SummaryMetricsReport;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

public class FlintAdapterTest {

    private static final String DUMMY_CHECK = "DUMMY_CHECK";

    private static final String DUMMY_ID = "DUMMY_ID";

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    private static final String REF_VALUE = "409";

    private static final String TUM_VALUE = "309";

    @Test
    public void verifyAdapterRunning(@Mocked final HealthCheckerImpl insertSize,
                    @Mocked final HealthCheckerImpl summaryMetric, @Mocked final HealthCheckerImpl coverage,
                    @Mocked final JsonReport report) {

        new NonStrictExpectations() {

            {
                JsonReport.getInstance();
                result = report;
                times = 1;

                new HealthCheckerImpl(CheckType.INSERT_SIZE, anyString, (InsertSizeMetricsExtractor) any);
                result = insertSize;
                times = 1;
                insertSize.runCheck();
                returns(getInsertSizeDummyReport());
                times = 1;

                new HealthCheckerImpl(CheckType.SUMMARY_METRICS, anyString, (SummaryMetricsExtractor) any);
                result = summaryMetric;
                times = 1;

                summaryMetric.runCheck();
                returns(getSummaryMetricsDummyReport());
                times = 1;

                new HealthCheckerImpl(CheckType.COVERAGE, anyString, (WGSExtractor) any);
                result = coverage;
                times = 1;

                coverage.runCheck();
                returns(getCoverageDummyReport());
                times = 1;
            }
        };
        final HealthCheckAdapter adapter = new FlintAdapter();
        adapter.runCheck(DUMMY_RUN_DIR);

        new Verifications() {

            {
                report.addReportData((BaseReport) any);
                times = 3;
            }
        };
    }

    private BaseReport getInsertSizeDummyReport() {
        final BaseDataReport testDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, REF_VALUE);
        final BaseDataReport secTestDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, TUM_VALUE);

        return new InsertSizeMetricsReport(CheckType.INSERT_SIZE, Arrays.asList(testDataReport),
                        Arrays.asList(secTestDataReport));
    }

    private BaseReport getSummaryMetricsDummyReport() {
        final BaseDataReport testDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, REF_VALUE);
        final BaseDataReport secTestDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, TUM_VALUE);

        return new SummaryMetricsReport(CheckType.SUMMARY_METRICS, Arrays.asList(testDataReport),
                        Arrays.asList(secTestDataReport));
    }

    private BaseReport getCoverageDummyReport() {
        final BaseDataReport testDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, REF_VALUE);
        final BaseDataReport secTestDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, TUM_VALUE);

        return new SummaryMetricsReport(CheckType.COVERAGE, Arrays.asList(testDataReport),
                        Arrays.asList(secTestDataReport));
    }
}
