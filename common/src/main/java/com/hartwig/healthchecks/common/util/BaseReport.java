package com.hartwig.healthchecks.common.util;

import java.io.Serializable;

public class BaseReport implements Serializable {

    private static final long serialVersionUID = -4752339157661751000L;

    private final CheckType checkType;

    public BaseReport(final CheckType checkType) {
        this.checkType = checkType;
    }

    public CheckType getCheckType() {
        return checkType;
    }

    @Override
    public String toString() {
        return "BaseConfig{" + "checkType=" + checkType + '}';
    }
}
