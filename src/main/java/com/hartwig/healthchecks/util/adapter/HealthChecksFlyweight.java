package com.hartwig.healthchecks.util.adapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.boggs.adapter.BoggsAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.exception.NotFoundException;
import com.hartwig.healthchecks.common.util.CheckType;


public class HealthChecksFlyweight {

	private static Map<CheckType, HealthCheckAdapter> flyweight = new HashMap<>();

	static {
		flyweight.computeIfAbsent(CheckType.BOGGS, adapter -> new BoggsAdapter());
	}

	private static HealthChecksFlyweight instance = new HealthChecksFlyweight();

	private HealthChecksFlyweight() {
	}

	public static HealthChecksFlyweight getInstance() {
		return instance;
	}

	public HealthCheckAdapter getAdapter(@NotNull String type) throws NotFoundException {
		Optional<CheckType> checkType = CheckType.getByType(type);
		if (!checkType.isPresent()) {
			throw new NotFoundException(String.format("Invalid CheckType informed %s", type));
		}
		return flyweight.get(checkType.get());
	}
}