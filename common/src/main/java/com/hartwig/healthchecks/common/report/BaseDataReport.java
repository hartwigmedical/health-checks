package com.hartwig.healthchecks.common.report;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class BaseDataReport {

    private static final String LOG_MSG = "Check '%s' for sample '%s' has value '%s'";

    @NotNull
    private final String sampleId;
    @NotNull
    private final String checkName;
    @NotNull
    private final String value;

    public BaseDataReport(@NotNull final String sampleId, @NotNull final String checkName,
            @NotNull final String value) {
        this.sampleId = sampleId;
        this.checkName = checkName;
        this.value = value;
    }

    @NotNull
    public String getSampleId() {
        return sampleId;
    }

    @NotNull
    public String getCheckName() {
        return checkName;
    }

    @NotNull
    public String getValue() {
        return value;
    }

    public void log(@NotNull Logger logger) {
        logger.info(String.format(LOG_MSG, checkName, sampleId, value));
    }

    public static void log(@NotNull Logger logger, @NotNull List<BaseDataReport> reports) {
        for (BaseDataReport report : reports) {
            report.log(logger);
        }
    }
}
