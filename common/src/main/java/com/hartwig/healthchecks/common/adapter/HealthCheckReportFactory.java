package com.hartwig.healthchecks.common.adapter;

import com.hartwig.healthchecks.common.report.Report;

@FunctionalInterface
public interface HealthCheckReportFactory {

    Report create();
}
