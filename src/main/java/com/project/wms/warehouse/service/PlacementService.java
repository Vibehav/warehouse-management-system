package com.project.wms.warehouse.service;

import com.project.wms.catalogue.domain.ProductSku;
import com.project.wms.common.enums.ConditionType;
import com.project.wms.warehouse.domain.Location;
import com.project.wms.warehouse.domain.LocationAllocation;
import com.project.wms.warehouse.domain.PlacementRule;
import com.project.wms.warehouse.dto.placementDTO.CreatePlacementRuleRequestDto;
import com.project.wms.warehouse.dto.placementDTO.PlacementRuleResponseDto;
import com.project.wms.warehouse.exception.LocationNotFoundException;
import com.project.wms.warehouse.exception.PlacementRuleNotFoundException;
import com.project.wms.warehouse.repository.PlacementRuleRepository;
import com.project.wms.warehouse.service.strategy.PlacementStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlacementService {

    private final CandidateLocationService candidateLocationService;
    private final PlacementRuleResolver placementRuleResolver;
    private final PlacementStrategyResolver strategyResolver;
    private final PlacementRuleRepository placementRuleRepository;


    public List<LocationAllocation> decidePlacement(Long warehouseId, ProductSku sku, int requiredQuantity) {

        // Step2: Fetch all the eligible locations
        List<Location> eligibleLocations = candidateLocationService.findEligibleLocations(warehouseId, sku);

        if (eligibleLocations.isEmpty()) {
            throw new LocationNotFoundException(
                    "No eligible location in warehouse " + warehouseId + " for SKU " + sku.getSkuCode());
        }

        // Step 3a: Choose the Strategy applies to this SKU by evaluating PlacementRule.
        String strategyType = placementRuleResolver.resolveStrategyType(sku);

        // Step 3b: Resolve that string to the actual Strategy bean via Spring's
        PlacementStrategy strategy = strategyResolver.resolve(strategyType);

        // Step 4: Run the chosen strategy against the eligible candidates.
        return strategy.allocate(sku, eligibleLocations, requiredQuantity);
    }



    @Transactional
    public PlacementRuleResponseDto create(CreatePlacementRuleRequestDto request) {
        validate(request);
        PlacementRule rule = new PlacementRule();
        rule.setConditionType(request.conditionType());

        if (request.conditionValue() != null) {
            rule.setConditionValue(request.conditionValue().toUpperCase());
        }

        rule.setStrategyType(request.strategyType());
        rule.setPriority(request.priority());
        rule.setActive(true);
        PlacementRule saved = placementRuleRepository.save(rule);
        return PlacementRuleResponseDto.from(saved);
    }

    public List<PlacementRuleResponseDto> getAll() {

        return placementRuleRepository
                .findByActiveTrueOrderByPriorityAsc()
                .stream()
                .map(PlacementRuleResponseDto::from)
                .toList();
    }

    public PlacementRuleResponseDto getById(Long id) {
        PlacementRule rule = placementRuleRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new PlacementRuleNotFoundException("Placement rule not found: " + id));
        return PlacementRuleResponseDto.from(rule);
    }

    @Transactional
    public void deactivate(Long id) {
        PlacementRule rule = placementRuleRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new PlacementRuleNotFoundException("Placement rule not found: " + id));
        rule.setActive(false);
    }

    @Transactional
    public void activate(Long id) {
        PlacementRule rule = placementRuleRepository.findById(id).orElseThrow(() -> new PlacementRuleNotFoundException("Placement rule not found: " + id));
        rule.setActive(true);
    }

    private void validate(CreatePlacementRuleRequestDto request) {

        if (request.conditionType() == ConditionType.CATEGORY_MATCH
                && (request.conditionValue() == null || request.conditionValue().isBlank())) {
            throw new IllegalArgumentException("conditionValue is required when conditionType is CATEGORY_MATCH");
        }

        if (request.conditionType() == ConditionType.DEFAULT && request.conditionValue() != null) {
            throw new IllegalArgumentException("conditionValue must be null when conditionType is DEFAULT");
        }

        if (request.priority() < 0) {
            throw new IllegalArgumentException("priority cannot be negative");
        }

        if (request.strategyType() == null || request.strategyType().isBlank()) {
            throw new IllegalArgumentException("strategyType is required");
        }
    }

}
