package com.hartwig.healthchecks.util.adapter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.exception.NotFoundException;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.util.CheckCategory;

public final class HealthChecksFlyweight {

    private static final Logger LOGGER = LogManager.getLogger(HealthChecksFlyweight.class);

    private static final Map<CheckCategory, HealthCheckAdapter> FLYWEIGHT = new HashMap<>();

    private static final HealthChecksFlyweight INSTANCE = new HealthChecksFlyweight();

    private static final Reflections BASE = new Reflections("com.hartwig.healthchecks.boggs.adapter");

    @SuppressWarnings("rawtypes")
    private static final Set<Class<? extends HealthCheckAdapter>> BASE_SET = BASE
                    .getSubTypesOf(HealthCheckAdapter.class);

    static {
        BASE_SET.stream().forEach(adapter -> {
            try {
                final HealthCheckAdapter adapterInstance = adapter.newInstance();
                final ResourceWrapper resourceWrapper = adapter.getAnnotation(ResourceWrapper.class);
                final CheckCategory checkCategory = resourceWrapper.type();

                FLYWEIGHT.put(checkCategory, adapterInstance);
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error(String.format("Error occurred when instantiating adapter. Error -> %s", e.getMessage()));
            }
        });
    }

    private HealthChecksFlyweight() {
    }

    public static HealthChecksFlyweight getInstance() {
        return INSTANCE;
    }

    @NotNull
    public HealthCheckAdapter getAdapter(@NotNull final String type) throws NotFoundException {
        final Optional<CheckCategory> checkType = CheckCategory.getByCategory(type);
        if (!checkType.isPresent()) {
            throw new NotFoundException(String.format("Invalid CheckCategory informed %s", type));
        }
        return FLYWEIGHT.get(checkType.get());
    }

    public Collection<HealthCheckAdapter> getAllAdapters() {
        return FLYWEIGHT.values();
    }
}
