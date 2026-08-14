package com.project.wms.warehouse.dto.locationDTO;

import com.project.wms.common.enums.StorageZoneType;

public record CreateLocationRequestDto(
        String code,
        Integer capacity,
        StorageZoneType storageZoneType,
        Integer sequenceOrder
) {}
