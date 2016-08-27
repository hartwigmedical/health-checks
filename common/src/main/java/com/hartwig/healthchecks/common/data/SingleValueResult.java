package com.hartwig.healthchecks.common.data;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;

import org.jetbrains.annotations.NotNull;

public class SingleValueResult extends BaseResult {

    private static final long serialVersionUID = -5744830259786248569L;

    @NotNull
    private final HealthCheck check;

    public SingleValueResult(@NotNull final CheckType checkType, @NotNull final HealthCheck check) {
        super(checkType);
        this.check = check;
    }

    @NotNull
    public HealthCheck getCheck() {
        return check;
    }
}
