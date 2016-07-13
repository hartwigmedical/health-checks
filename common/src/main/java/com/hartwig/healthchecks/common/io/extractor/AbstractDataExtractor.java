package com.hartwig.healthchecks.common.io.extractor;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hartwig.healthchecks.common.report.BaseDataReport;

public abstract class AbstractDataExtractor implements DataExtractor {

    public static final String PASS = "PASS";

    public static final String WARN = "WARN";

    public static final String FAIL = "FAIL";

    protected static final int ONE = 1;

    protected static final int NEGATIVE_ONE = -1;

    protected static final int ZERO = 0;

    protected static final double ZERO_DOUBLE_VALUE = 0.0d;

    protected static final String FILE_NOT_FOUND = "File %s was not found";

    protected static final String SAMPLE_PREFIX = "CPCT";

    protected static final String REF_SAMPLE_SUFFIX = "R";

    protected static final String TUM_SAMPLE_SUFFIX = "T";

    protected static final String EMPTY_FILES_ERROR = "File %s was found empty in path -> %s";

    protected static final String LINE_NOT_FOUND_ERROR = "File %s does not contain line with value %s";

    protected static final String FILE_NOT_FOUND_ERROR = "File with prefix %s and suffix %s was not found in path %s";

    protected static final String HEADER_NOT_FOUND_ERROR = "File %s does not contain following headers %s";

    protected static final String MALFORMED_FILE_MSG = "Malformed %s file is path %s was expecting %s in file";

    protected static final String SEPERATOR_REGEX = "\t";

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

    protected static final String COMMA_DELIMITER = ",";

    private static final String LOG_MSG = "Check '%s' for Patient '%s' has value '%s'";

    private static final Logger LOGGER = LogManager.getLogger(AbstractDataExtractor.class);

    protected void logBaseDataReports(final List<BaseDataReport> baseDataReports) {
        baseDataReports.forEach((baseDataReport) -> {
            logBaseDataReport(baseDataReport);
        });
    }

    protected void logBaseDataReport(final BaseDataReport baseDataReport) {
        LOGGER.info(String.format(LOG_MSG, baseDataReport.getCheckName(), baseDataReport.getPatientId(),
                        baseDataReport.getValue()));
    }
}
