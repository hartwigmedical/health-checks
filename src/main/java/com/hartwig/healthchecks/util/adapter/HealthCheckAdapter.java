package com.hartwig.healthchecks.util.adapter;

import java.io.IOException;

public interface HealthCheckAdapter {
  void runCheck(String runDirectory) throws IOException;
}
