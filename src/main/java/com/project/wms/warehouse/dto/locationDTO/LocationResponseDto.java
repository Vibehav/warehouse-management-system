package com.project.wms.warehouse.dto.locationDTO;

import com.project.wms.common.enums.StorageZoneType;

public record LocationResponseDto(
        Long id, String code, Integer capacity,
        StorageZoneType storageZoneType, Integer sequenceOrder, boolean active, boolean blocked
) {
    public static LocationResponseDto from(com.project.wms.warehouse.domain.Location l) {
        return new LocationResponseDto(l.getId(), l.getCode(), l.getCapacity(),
                l.getStorageZoneType(), l.getSequenceOrder(), l.isActive(), l.isBlocked());
    }
}
