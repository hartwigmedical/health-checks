package com.hartwig.healthchecks.util.adapter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.exception.NotFoundException;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

public final class HealthChecksFlyweight {

    private static final Logger LOGGER = LogManager.getLogger(HealthChecksFlyweight.class);

    private static final Map<CheckCategory, AbstractHealthCheckAdapter> FLYWEIGHT = new HashMap<>();

    private static final HealthChecksFlyweight INSTANCE = new HealthChecksFlyweight();

    private static final Reflections BASE = new Reflections("com.hartwig.healthchecks");

    @SuppressWarnings("rawtypes")
    private static final Set<Class<? extends AbstractHealthCheckAdapter>> BASE_SET = BASE
                    .getSubTypesOf(AbstractHealthCheckAdapter.class);

    static {
        BASE_SET.stream().forEach(adapter -> {
            try {
                final AbstractHealthCheckAdapter adapterInstance = adapter.newInstance();
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
    public AbstractHealthCheckAdapter getAdapter(@NotNull final String type) throws NotFoundException {
        final Optional<CheckCategory> checkType = CheckCategory.getByCategory(type);
        if (!checkType.isPresent()) {
            throw new NotFoundException(String.format("Invalid CheckCategory informed %s", type));
        }
        return FLYWEIGHT.get(checkType.get());
    }

    public Collection<AbstractHealthCheckAdapter> getAllAdapters() {
        return FLYWEIGHT.values();
    }
}
