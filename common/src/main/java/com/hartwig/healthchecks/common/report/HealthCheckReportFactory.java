package com.hartwig.healthchecks.common.report;

@FunctionalInterface
public interface HealthCheckReportFactory {

    Report create();
}
