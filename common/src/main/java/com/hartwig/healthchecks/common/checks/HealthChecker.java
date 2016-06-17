package com.hartwig.healthchecks.common.checks;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.BaseReport;

import java.io.IOException;

public interface HealthChecker {

    BaseReport runCheck() throws IOException, EmptyFileException;
}
