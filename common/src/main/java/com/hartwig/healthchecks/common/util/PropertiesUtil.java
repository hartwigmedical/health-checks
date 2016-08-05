package com.hartwig.healthchecks.common.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class PropertiesUtil {

    private static final Logger LOGGER = LogManager.getLogger(PropertiesUtil.class);

    private static final String CONFIG_FILE = "config.properties";
    private static final String ERROR_LOADING_PROPERTIES = "Error loading PROPERTIES. Error -> %s";

    private static final PropertiesUtil INSTANCE = new PropertiesUtil();
    private static final Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(ClassLoader.getSystemResourceAsStream(CONFIG_FILE));
        } catch (IOException e) {
            LOGGER.error(String.format(ERROR_LOADING_PROPERTIES, e.getMessage()));
        }
    }

    private PropertiesUtil() {
    }

    @NotNull
    public static PropertiesUtil getInstance() {
        return INSTANCE;
    }

    @NotNull
    public String getProperty(@NotNull final String key) {
        return PROPERTIES.getProperty(key);
    }
}
