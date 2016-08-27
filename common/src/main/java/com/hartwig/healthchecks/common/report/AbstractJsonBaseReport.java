package com.hartwig.healthchecks.common.report;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.data.BaseResult;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.PathRegexFinder;
import com.hartwig.healthchecks.common.io.reader.LineReader;
import com.hartwig.healthchecks.common.report.metadata.MetadataExtractor;
import com.hartwig.healthchecks.common.report.metadata.ReportMetadata;
import com.hartwig.healthchecks.common.util.PropertiesUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class AbstractJsonBaseReport implements Report {

    static final Gson GSON = new GsonBuilder().setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).disableHtmlEscaping().create();

    private static final String ADD_META_DATA = "add.metadata";
    private static final String ADD_META_DATA_TRUE = "1";

    private static final String PIPELINE_VERSION = "PipelineVersion";
    private static final String RUN_DATE = "RunDate";
    private static final String METADATA_ERR_MSG = "Error occurred whilst extracting metadata. "
                    + "Will continue with report generation anyway. Error -> %s ";

    private static final Logger LOGGER = LogManager.getLogger(Report.class);

    private static final Map<CheckType, BaseResult> HEALTH_CHECKS = new ConcurrentHashMap<>();

    @Override
    public void addReportData(@NotNull final BaseResult reportData) {
        HEALTH_CHECKS.putIfAbsent(reportData.getCheckType(), reportData);
    }

    JsonArray computeElements(@NotNull final String runDirectory) {
        final JsonArray reportArray = new JsonArray();
        final PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();

        final String addMetaData = propertiesUtil.getProperty(ADD_META_DATA);

        if (addMetaData.equals(ADD_META_DATA_TRUE)) {
            final JsonObject element = getMetadata(runDirectory);
            if (element != null) {
                reportArray.add(element);
            }
        }

        HEALTH_CHECKS.forEach((checkType, baseReport) -> {
            final JsonElement configJson = GSON.toJsonTree(baseReport);

            final JsonObject element = new JsonObject();
            element.add(checkType.toString(), configJson);

            reportArray.add(element);
        });

        return reportArray;
    }

    @Nullable
    private static JsonObject getMetadata(final String runDirectory) {
        JsonObject element = null;

        try {
            final MetadataExtractor metadataExtractor = new MetadataExtractor(PathRegexFinder.build(),
                    LineReader.build());
            final ReportMetadata reportMetadata = metadataExtractor.extractMetadata(runDirectory);

            final JsonParser parser = new JsonParser();
            element = new JsonObject();
            element.add(RUN_DATE, parser.parse(reportMetadata.getDate()));
            element.add(PIPELINE_VERSION, parser.parse(reportMetadata.getPipelineVersion()));
        } catch (IOException | HealthChecksException e) {
            LOGGER.error(String.format(METADATA_ERR_MSG, e.getMessage()));
        }

        return element;
    }
}
