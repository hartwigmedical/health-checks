package com.hartwig.healthchecks.common.report;

import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hartwig.healthchecks.common.exception.GenerateReportException;

import org.jetbrains.annotations.NotNull;

public final class StandardOutputReport extends AbstractJsonBaseReport {

    private static final StandardOutputReport INSTANCE = new StandardOutputReport();

    private StandardOutputReport() {
    }

    public static StandardOutputReport getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public Optional<String> generateReport(@NotNull final String runDirectory) throws GenerateReportException {
        final JsonArray reportArray = computeElements(runDirectory);

        final JsonObject reportJson = new JsonObject();
        reportJson.add("health_checks", reportArray);

        return Optional.ofNullable(GSON.toJson(reportJson));
    }
}
