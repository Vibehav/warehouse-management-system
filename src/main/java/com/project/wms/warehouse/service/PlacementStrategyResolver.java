package com.project.wms.warehouse.service;

import com.project.wms.warehouse.service.strategy.PlacementStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlacementStrategyResolver {

    private final Map<String, PlacementStrategy> strategiesByType;

    public PlacementStrategy resolve(String strategyType) {
        PlacementStrategy strategy = strategiesByType.get(strategyType);
        if (strategy == null) {
            throw new IllegalStateException("No strategy registered for type: " + strategyType);
        }
        return strategy;
    }
}