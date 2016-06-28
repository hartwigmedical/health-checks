package com.hartwig.healthchecks.common.extractor;

public abstract class BaseDataExtractor implements DataExtractor {

    public static final String PASS = "PASS";

    public static final String WARN = "WARN";

    public static final String FAIL = "FAIL";

    protected static final int ONE = 1;

    protected static final int NEGATIVE_ONE = -1;

    protected static final int ZERO = 0;

    protected static final double ZERO_DOUBLE_VALUE = 0.0d;

    protected static final String ZIP_FILES_SUFFIX = ".zip";

    protected static final String FILE_NOT_FOUND = "File %s was not found";

    protected static final String SAMPLE_PREFIX = "CPCT";

    protected static final String REF_SAMPLE_SUFFIX = "R";

    protected static final String TUM_SAMPLE_SUFFIX = "T";

    protected static final String EMPTY_FILES_ERROR = "File %s was found empty in path -> %s";

    protected static final String FILE_NOT_FOUND_ERROR = "File with prefix %s and suffix %s was not found in path %s";

    protected static final String SEPERATOR_REGEX = "\t";

    protected static final String FLAGSTAT_SUFFIX = ".flagstat";

    protected static final String REALIGN = "realign";

    protected static final Double HUNDRED_FACTOR = 100D;

    protected static final Integer DOUBLE_SEQUENCE = 2;
}
