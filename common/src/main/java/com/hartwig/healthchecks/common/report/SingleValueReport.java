package com.hartwig.healthchecks.common.report;

import com.hartwig.healthchecks.common.checks.CheckType;

import org.jetbrains.annotations.NotNull;

public class SingleValueReport extends BaseReport {

    private static final long serialVersionUID = -5744830259786248569L;

    @NotNull
    private final HealthCheck check;

    public SingleValueReport(@NotNull final CheckType checkType, @NotNull final HealthCheck check) {
        super(checkType);
        this.check = check;
    }

    @NotNull
    public HealthCheck getCheck() {
        return check;
    }
}
