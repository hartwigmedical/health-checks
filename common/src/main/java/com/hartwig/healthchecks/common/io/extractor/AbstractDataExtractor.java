package com.hartwig.healthchecks.common.io.extractor;

import java.util.List;

import com.hartwig.healthchecks.common.report.BaseDataReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractDataExtractor implements DataExtractor {

    protected static final int ONE = 1;
    protected static final int NEGATIVE_ONE = -1;
    protected static final int ZERO = 0;
    protected static final double ZERO_DOUBLE_VALUE = 0.0d;

    protected static final String SAMPLE_PREFIX = "CPCT";
    protected static final String REF_SAMPLE_SUFFIX = "R";
    protected static final String TUM_SAMPLE_SUFFIX = "T";

    protected static final String EMPTY_FILES_ERROR = "File %s was found empty in path -> %s";
    protected static final String HEADER_NOT_FOUND_ERROR = "File %s does not contain following headers %s";
    protected static final String MALFORMED_FILE_MSG = "Malformed %s file is path %s was expecting %s in file";
    protected static final String SEPARATOR_REGEX = "\t";
    protected static final String FLAGSTAT_SUFFIX = ".flagstat";
    protected static final Double HUNDRED_FACTOR = 100D;
    protected static final Integer DOUBLE_SEQUENCE = 2;
    protected static final String DEDUP_SAMPLE_SUFFIX = "dedup";
    protected static final String UNDER_SCORE = "_";
    protected static final String HASH = "#";
    protected static final String QC_STATS = "QCStats";
    protected static final String BIGGER_THAN = ">";
    protected static final String SMALLER_THAN = "<";
    protected static final String PLUS = "+";
    protected static final String DOT = ".";
    protected static final String EQUAL = "=";
    protected static final String COMMA_DELIMITER = ",";
    protected static final String SEMICOLON_DELIMITER = ";";
    protected static final String DASH = "-";

    private static final String LOG_MSG = "Check '%s' for sample '%s' has value '%s'";

    private static final Logger LOGGER = LogManager.getLogger(AbstractDataExtractor.class);

    protected void logBaseDataReports(@NotNull final List<BaseDataReport> baseDataReports) {
        baseDataReports.forEach(this::logBaseDataReport);
    }

    protected void logBaseDataReport(@NotNull final BaseDataReport baseDataReport) {
        LOGGER.info(String.format(LOG_MSG, baseDataReport.getCheckName(), baseDataReport.getSampleId(),
                baseDataReport.getValue()));
    }

    protected static void logBaseDataReport(@NotNull final Logger logger,
            @NotNull final BaseDataReport baseDataReport) {
        logger.info(String.format(LOG_MSG, baseDataReport.getCheckName(), baseDataReport.getSampleId(),
                baseDataReport.getValue()));
    }
}
