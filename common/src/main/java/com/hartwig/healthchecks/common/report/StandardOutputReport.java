package com.hartwig.healthchecks.common.report;

import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hartwig.healthchecks.common.exception.GenerateReportException;
import com.hartwig.healthchecks.common.util.PropertiesUtil;

import org.jetbrains.annotations.NotNull;

public final class StandardOutputReport extends JsonBaseReport {

    private static final StandardOutputReport INSTANCE = new StandardOutputReport();

    private StandardOutputReport() {
    }

    public static StandardOutputReport getInstance() {
        return INSTANCE;
    }

    @Override
    public void addReportData(@NotNull final BaseReport reportData) {
        HEALTH_CHECKS.putIfAbsent(reportData.getCheckType(), reportData);
    }

    @NotNull
    @Override
    public Optional<String> generateReport(final String runDirectory) throws GenerateReportException {
        final JsonArray reportArray = new JsonArray();
        final PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();

        final String parseLogs = propertiesUtil.getProperty(PARSE_LOGS);

        if (parseLogs != null && parseLogs.equals(TRUE)) {
            final JsonObject element = getMetadata(runDirectory);
            if (element != null) {
                reportArray.add(element);
            }
        }

        computeElements(reportArray);

        final JsonObject reportJson = new JsonObject();
        reportJson.add("health_checks", reportArray);

        return Optional.ofNullable(GSON.toJson(reportJson));
    }
}
