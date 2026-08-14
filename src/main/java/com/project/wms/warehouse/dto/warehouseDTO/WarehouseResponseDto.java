package com.project.wms.warehouse.dto.warehouseDTO;

public record WarehouseResponseDto(Long id, String name, String code, boolean active) {
    public static WarehouseResponseDto from(com.project.wms.warehouse.domain.Warehouse w) {
        return new WarehouseResponseDto(w.getId(), w.getName(), w.getCode(), w.isActive());
    }
}
