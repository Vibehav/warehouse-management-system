package com.project.wms.warehouse.service;

import com.project.wms.catalogue.domain.ProductSku;
import com.project.wms.warehouse.domain.PlacementRule;
import com.project.wms.warehouse.repository.PlacementRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlacementRuleResolver {

    private final PlacementRuleRepository ruleRepository;

    // To place a product we need to know its storage type first.
    public String resolveStrategyType(ProductSku sku) {
        List<PlacementRule> rules = ruleRepository.findByActiveTrueOrderByPriorityAsc();

        for (PlacementRule rule : rules) {
            if (matches(rule, sku)) {
                return rule.getStrategyType();
            }
        }
        throw new IllegalStateException("No placement rule matched SKU " + sku.getSkuCode());
    }

    private boolean matches(PlacementRule rule, ProductSku sku) {
        return switch (rule.getConditionType()) {
            case AMBIENT, CHILLED, FROZEN, HAZMAT -> sku.getCategory() != null
                       && sku.getCategory().equals(rule.getCategory());
        };
    }
}