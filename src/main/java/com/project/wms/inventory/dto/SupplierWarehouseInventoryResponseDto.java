package com.project.wms.inventory.dto;

import java.util.List;

/** A supplier's balances within one warehouse. */
public record SupplierWarehouseInventoryResponseDto(
        Long warehouseId,
        String warehouseCode,
        List<InventoryBalanceResponseDto> balances) {
}
