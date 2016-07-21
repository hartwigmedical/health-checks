package com.hartwig.healthchecks.common.report;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.PathRegexFinder;
import com.hartwig.healthchecks.common.io.reader.LineReader;
import com.hartwig.healthchecks.common.report.metadata.MetadataExtractor;
import com.hartwig.healthchecks.common.report.metadata.ReportMetadata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public abstract class JsonBaseReport implements Report {

    protected static final String METADATA_ERR_MSG = "Error occurred whilst extracting metada."
                    + "Will continue with report generation anyway. Error -> %s ";

    protected static final String ERROR_GENERATING_REPORT = "Error occurred whilst generating reports. Error -> %s";

    protected static final String PIPE_LINE_VERSION = "PipeLineVersion";

    protected static final String RUN_DATE = "RunDate";

    protected static final String TRUE = "1";

    protected static final String PARSE_LOGS = "parse.logs";

    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).disableHtmlEscaping().create();

    protected static final Logger LOGGER = LogManager.getLogger(Report.class);

    protected static final Map<CheckType, BaseReport> HEALTH_CHECKS = new ConcurrentHashMap<>();

    @Override
    public void addReportData(@NotNull final BaseReport reportData) {
        HEALTH_CHECKS.putIfAbsent(reportData.getCheckType(), reportData);
    }

    protected JsonObject getMetadata(final String runDirectory) {
        JsonObject element = null;

        try {
            final MetadataExtractor metadataExtractor = new MetadataExtractor(PathRegexFinder.build(),
                            LineReader.build());
            final ReportMetadata reportMetadata = metadataExtractor.extractMetadata(runDirectory);
            final JsonParser parser = new JsonParser();
            element = new JsonObject();
            element.add(RUN_DATE, parser.parse(reportMetadata.getDate()));
            element.add(PIPE_LINE_VERSION, parser.parse(reportMetadata.getPipelineVersion()));
        } catch (IOException | HealthChecksException e) {
            LOGGER.error(String.format(METADATA_ERR_MSG, e.getMessage()));
        }

        return element;
    }
}
