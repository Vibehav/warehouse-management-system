package com.project.wms.inventory.dto;

import com.project.wms.inventory.domain.InventoryBalance;
import com.project.wms.inventory.domain.InventoryLot;

import java.time.LocalDate;

public record InventoryBalanceResponseDto(
        Long balanceId,
        String skuCode,
        String skuName,
        String supplierCode,
        String warehouseCode,
        String locationCode,
        String batchNo,
        LocalDate expiryDate,
        int quantity,
        int reservedQuantity,
        int availableQuantity,
        String lotState
) {
    public static InventoryBalanceResponseDto from(InventoryBalance balance) {
        InventoryLot lot = balance.getInventoryLot();
        return new InventoryBalanceResponseDto(
                balance.getId(),
                lot.getProductSku().getSkuCode(),
                lot.getProductSku().getName(),
                lot.getSupplier().getCode(),
                lot.getWarehouse().getCode(),
                balance.getLocation().getCode(),
                lot.getBatchNo(),
                lot.getExpiryDate(),
                balance.getQuantity(),
                balance.getReservedQuantity(),
                balance.availableQuantity(),
                lot.getState().name()
        );
    }
}
