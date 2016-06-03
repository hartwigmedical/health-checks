package com.hartwig.healthchecks.common.adapter;

import java.io.IOException;

public interface HealthCheckAdapter {
    void runCheck(String runDirectory) throws IOException;
}
