package com.project.wms.warehouse.dto.placementDTO;

import com.project.wms.common.enums.ConditionType;

public record CreatePlacementRuleRequestDto(
        ConditionType conditionType,
        String conditionValue, // null when conditionType = DEFAULT
        String strategyType,   // "FIRST_AVAILABLE" or "NEAREST_AVAILABLE" — must match a @Component bean name
        int priority
) {}