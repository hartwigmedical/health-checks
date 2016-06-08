package com.hartwig.healthchecks.common.util;

import org.junit.Assert;
import org.junit.Test;

public class PropertiesUtilTest {

    @Test
    public void getProperty() throws Exception {
        PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();
        String dir = propertiesUtil.getProperty("report.dir");

        Assert.assertEquals("Directory is not the one expected", "/tmp", dir);
    }

}