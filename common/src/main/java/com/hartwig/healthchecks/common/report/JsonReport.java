package com.hartwig.healthchecks.common.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.GenerateReportException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.PathRegexFinder;
import com.hartwig.healthchecks.common.io.reader.LineReader;
import com.hartwig.healthchecks.common.report.metadata.MetadataExtractor;
import com.hartwig.healthchecks.common.report.metadata.ReportMetadata;
import com.hartwig.healthchecks.common.util.PropertiesUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class JsonReport implements Report {

    private static final String METADATA_ERR_MSG = "Error occurred whilst extracting metada."
                    + "Will continue with report generation anyway. Error -> %s ";

    private static final String ERROR_GENERATING_REPORT = "Error occurred whilst generating reports. Error -> %s";

    private static final String PIPE_LINE_VERSION = "PipeLineVersion";

    private static final String RUN_DATE = "RunDate";

    private static final String TRUE = "1";

    private static final String REPORT_DIR = "report.dir";

    private static final String PARSE_LOGS = "parse.logs";

    private static final String REPORT_NAME = "health-checks_%s.json";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).disableHtmlEscaping().create();

    private static final Logger LOGGER = LogManager.getLogger(Report.class);

    private static final JsonReport INSTANCE = new JsonReport();

    private static final Map<CheckType, BaseReport> HEALTH_CHECKS = new ConcurrentHashMap<>();

    private JsonReport() {
    }

    public static JsonReport getInstance() {
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

        HEALTH_CHECKS.forEach((checkType, baseReport) -> {
            final JsonElement configJson = GSON.toJsonTree(baseReport);

            final JsonObject element = new JsonObject();
            element.add(checkType.toString(), configJson);

            reportArray.add(element);
        });

        final String reportDir = propertiesUtil.getProperty(REPORT_DIR);
        final String fileName = String.format("%s/%s", reportDir,
                        String.format(REPORT_NAME, System.currentTimeMillis()));

        try (FileWriter fileWriter = new FileWriter(new File(fileName))) {
            final JsonObject reportJson = new JsonObject();
            reportJson.add("health_checks", reportArray);
            fileWriter.write(GSON.toJson(reportJson));
            fileWriter.flush();
        } catch (final IOException e) {
            LOGGER.error(String.format(ERROR_GENERATING_REPORT, e.getMessage()));
            throw (GenerateReportException) new GenerateReportException(e.getMessage()).initCause(e);
        }
        return Optional.ofNullable(fileName);
    }

    private JsonObject getMetadata(final String runDirectory) {
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
