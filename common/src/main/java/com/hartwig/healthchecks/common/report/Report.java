package com.hartwig.healthchecks.common.report;

import com.hartwig.healthchecks.common.util.BaseReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public abstract class Report {

    protected static Logger LOGGER = LogManager.getLogger(Report.class);

    public abstract void addReportData(BaseReport reportData);

    public abstract Optional<String> generateReport() ;
}
