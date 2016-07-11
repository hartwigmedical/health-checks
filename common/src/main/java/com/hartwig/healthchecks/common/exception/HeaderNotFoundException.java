package com.hartwig.healthchecks.common.exception;

import org.jetbrains.annotations.NotNull;

public class HeaderNotFoundException extends HealthChecksException {

    private static final long serialVersionUID = -8396650626359037492L;

    public HeaderNotFoundException(@NotNull final String message) {
        super(message);
    }
}
