package com.project.wms.warehouse.service;

import com.project.wms.catalogue.domain.ProductSku;
import com.project.wms.warehouse.domain.Location;
import com.project.wms.warehouse.domain.LocationAllocation;
import com.project.wms.warehouse.exception.LocationNotFoundException;
import com.project.wms.warehouse.service.strategy.PlacementStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlacementService {

    private final CandidateLocationService candidateLocationService;
    private final PlacementRuleResolver placementRuleResolver;
    private final PlacementStrategyResolver strategyResolver;


    public List<LocationAllocation> decidePlacement(Long warehouseId, ProductSku sku, int requiredQuantity) {

        // Step2: Fetch all the eligible locations
        List<Location> eligibleLocations = candidateLocationService.findEligibleLocations(warehouseId, sku);

        if (eligibleLocations.isEmpty()) {
            throw new LocationNotFoundException(
                    "No eligible location in warehouse " + warehouseId + " for SKU " + sku.getSkuCode());
        }

        // Step 3a: Choose the Strategy applies to this SKU by evaluating PlacementRule.
        // Returns a string "FIRST_AVAILABLE" or "NEAREST_AVAILABLE" (Component bean name)
        String strategyType = placementRuleResolver.resolveStrategyType(sku);

        // Step 3b: Resolve that string to the actual Strategy bean via Spring's
        PlacementStrategy strategy = strategyResolver.resolve(strategyType);

        // Step 4: Run the chosen strategy against the eligible candidates.
        return strategy.allocate(sku, eligibleLocations, requiredQuantity);
    }
}