package com.hartwig.healthchecks.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertiesUtilTest {

    @Test
    public void getProperty() {
        final PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();
        final String dir = propertiesUtil.getProperty("report.dir");
        assertEquals("Directory is not the one expected", "/tmp", dir);
    }
}
