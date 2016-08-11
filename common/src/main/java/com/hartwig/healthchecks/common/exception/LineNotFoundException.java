package com.hartwig.healthchecks.common.exception;

import org.jetbrains.annotations.NotNull;

public class LineNotFoundException extends HealthChecksException {

    private static final long serialVersionUID = -8396650626359037492L;

    private static final String LINE_NOT_FOUND_ERROR = "File does not contain lines with value %s";

    // KODU: TODO: Remove constructor
    public LineNotFoundException(@NotNull final String filePath, @NotNull final String filter) {
        super(String.format(LINE_NOT_FOUND_ERROR, filter));
    }

    public LineNotFoundException(@NotNull final String filter) {
        super(String.format(LINE_NOT_FOUND_ERROR, filter));
    }
}
