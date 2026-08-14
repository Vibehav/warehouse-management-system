package com.project.wms.warehouse.dto.placementDTO;



import com.project.wms.common.enums.ConditionType;

public record PlacementRuleResponseDto(
        Long id, ConditionType conditionType, String conditionValue,
        String strategyType, int priority, boolean active
) {
    public static PlacementRuleResponseDto from(com.project.wms.warehouse.domain.PlacementRule rule) {
        return new PlacementRuleResponseDto(
                rule.getId(), rule.getConditionType(), rule.getConditionValue(),
                rule.getStrategyType(), rule.getPriority(), rule.isActive()
        );
    }
}