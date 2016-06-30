package com.hartwig.healthchecks.common.exception;

import org.jetbrains.annotations.NotNull;

public class LineNotFoundException extends HealthChecksException {

    private static final long serialVersionUID = -8396650626359037492L;

    public LineNotFoundException(@NotNull final String message) {
        super(message);
    }
}
