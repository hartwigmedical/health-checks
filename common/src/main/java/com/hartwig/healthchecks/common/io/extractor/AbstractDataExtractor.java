package com.hartwig.healthchecks.common.io.extractor;

import java.util.List;

import com.hartwig.healthchecks.common.report.BaseDataReport;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractDataExtractor implements DataExtractor {

    protected static final int ONE = 1;
    protected static final int ZERO = 0;
    protected static final double ZERO_DOUBLE_VALUE = 0.0d;

    protected static final String SAMPLE_PREFIX = "CPCT";
    protected static final String REF_SAMPLE_SUFFIX = "R";
    protected static final String TUM_SAMPLE_SUFFIX = "T";

    protected static final String SEPARATOR_REGEX = "\t";
    protected static final String DEDUP_SAMPLE_SUFFIX = "dedup";
    protected static final String UNDERSCORE = "_";
    protected static final String QC_STATS = "QCStats";

    private static final String LOG_MSG = "Check '%s' for sample '%s' has value '%s'";

    protected static void logBaseDataReports(@NotNull final Logger logger,
            @NotNull final List<BaseDataReport> baseDataReports) {
        for (BaseDataReport report : baseDataReports) {
            logBaseDataReport(logger, report);
        }
    }

    protected static void logBaseDataReport(@NotNull final Logger logger,
            @NotNull final BaseDataReport baseDataReport) {
        logger.info(String.format(LOG_MSG, baseDataReport.getCheckName(), baseDataReport.getSampleId(),
                baseDataReport.getValue()));
    }
}
