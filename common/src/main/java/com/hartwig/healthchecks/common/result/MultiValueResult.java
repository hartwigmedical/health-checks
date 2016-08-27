package com.hartwig.healthchecks.common.result;

import java.util.List;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;

import org.jetbrains.annotations.NotNull;

public class MultiValueResult extends BaseResult {

    private static final long serialVersionUID = 7396913735152649393L;

    @NotNull
    private final List<HealthCheck> checks;

    public MultiValueResult(@NotNull final CheckType checkType, @NotNull final List<HealthCheck> checks) {
        super(checkType);
        this.checks = checks;
    }

    @NotNull
    public List<HealthCheck> getChecks() {
        return checks;
    }
}
