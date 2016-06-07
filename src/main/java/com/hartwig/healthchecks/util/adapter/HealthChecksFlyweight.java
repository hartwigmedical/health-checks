package com.hartwig.healthchecks.util.adapter;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.exception.NotFoundException;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.util.CheckCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.*;


public class HealthChecksFlyweight {

    private static Logger LOGGER = LogManager.getLogger(HealthChecksFlyweight.class);

    private static Map<CheckCategory, HealthCheckAdapter> flyweight = new HashMap<>();
    private static HealthChecksFlyweight instance = new HealthChecksFlyweight();

    private static Reflections base = new Reflections("com.hartwig.healthchecks.boggs.adapter");
    @SuppressWarnings("rawtypes")
    private static Set<Class<? extends HealthCheckAdapter>> baseSet = base.getSubTypesOf(HealthCheckAdapter.class);

    static {
        baseSet.stream()
                .forEach(adapter -> {
                    try {
                        HealthCheckAdapter adapterInstance = adapter.newInstance();
                        ResourceWrapper resourceWrapper = adapter.getAnnotation(ResourceWrapper.class);
                        CheckCategory checkCategory = resourceWrapper.type();

                        flyweight.put(checkCategory, adapterInstance);
                    } catch (InstantiationException | IllegalAccessException e) {
                        LOGGER.error(String.format("Error occurred when instantiating adapter. Error -> %s", e.getMessage()));
                    }
                });
    }

    private HealthChecksFlyweight() {
    }

    public static HealthChecksFlyweight getInstance() {
        return instance;
    }

    public HealthCheckAdapter getAdapter(@NotNull String type) throws NotFoundException {
        Optional<CheckCategory> checkType = CheckCategory.getByCategory(type);
        if (!checkType.isPresent()) {
            throw new NotFoundException(String.format("Invalid CheckCategory informed %s", type));
        }
        return flyweight.get(checkType.get());
    }

    public Collection<HealthCheckAdapter> getAllAdapters() {
        return flyweight.values();
    }
}