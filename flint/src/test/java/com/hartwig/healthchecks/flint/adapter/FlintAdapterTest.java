package com.hartwig.healthchecks.flint.adapter;

import java.util.Collections;

import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.path.RunPathDataFactory;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.report.SampleReport;
import com.hartwig.healthchecks.flint.extractor.InsertSizeMetricsExtractor;
import com.hartwig.healthchecks.flint.extractor.SummaryMetricsExtractor;
import com.hartwig.healthchecks.flint.extractor.WGSMetricsExtractor;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

public class FlintAdapterTest {

    private static final String DUMMY_CHECK = "DUMMY_CHECK";
    private static final String DUMMY_ID = "DUMMY_ID";
    private static final String DUMMY_RUN_DIR = "DummyRunDir";
    private static final String DUMMY_REPORT = "DummyReport";
    private static final String REF_VALUE = "409";
    private static final String TUM_VALUE = "309";

    @Test
    public void verifyAdapterRunning(@Mocked final HealthCheckerImpl insertSize,
            @Mocked final HealthCheckerImpl summaryMetric, @Mocked final HealthCheckerImpl coverage,
            @Mocked final Report report, @Mocked HealthCheckReportFactory factory,
            @Mocked AbstractHealthCheckAdapter mock) {
        new NonStrictExpectations() {
            {
                AbstractHealthCheckAdapter.attachReport(DUMMY_REPORT);
                result = factory;
                times = 1;

                factory.create();
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

                new HealthCheckerImpl(CheckType.COVERAGE, anyString, (WGSMetricsExtractor) any);
                result = coverage;
                times = 1;

                coverage.runCheck();
                returns(getCoverageDummyReport());
                times = 1;
            }
        };
        final AbstractHealthCheckAdapter adapter = new FlintAdapter();
        adapter.runCheck(RunPathDataFactory.fromRunDirectory(DUMMY_RUN_DIR), DUMMY_REPORT);

        new Verifications() {
            {
                report.addReportData((BaseReport) any);
                times = 3;
            }
        };
    }

    @NotNull
    private static BaseReport getInsertSizeDummyReport() {
        final BaseDataReport testDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, REF_VALUE);
        final BaseDataReport secTestDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, TUM_VALUE);

        return new SampleReport(CheckType.INSERT_SIZE, Collections.singletonList(testDataReport),
                Collections.singletonList(secTestDataReport));
    }

    @NotNull
    private static BaseReport getSummaryMetricsDummyReport() {
        final BaseDataReport testDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, REF_VALUE);
        final BaseDataReport secTestDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, TUM_VALUE);

        return new SampleReport(CheckType.SUMMARY_METRICS, Collections.singletonList(testDataReport),
                Collections.singletonList(secTestDataReport));
    }

    @NotNull
    private static BaseReport getCoverageDummyReport() {
        final BaseDataReport testDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, REF_VALUE);
        final BaseDataReport secTestDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, TUM_VALUE);

        return new SampleReport(CheckType.COVERAGE, Collections.singletonList(testDataReport),
                Collections.singletonList(secTestDataReport));
    }
}
