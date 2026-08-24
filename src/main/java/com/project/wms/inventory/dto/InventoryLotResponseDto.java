package com.project.wms.inventory.dto;

import com.project.wms.inventory.domain.InventoryLot;

import java.time.LocalDate;

public record InventoryLotResponseDto(
        Long id,
        String skuCode,
        String supplierCode,
        String warehouseCode,
        String batchNo,
        LocalDate expiryDate,
        int quantity,
        String state) {

    public static InventoryLotResponseDto from(InventoryLot lot) {
        return new InventoryLotResponseDto(
                lot.getId(),
                lot.getProductSku().getSkuCode(),
                lot.getSupplier().getCode(),
                lot.getWarehouse().getCode(),
                lot.getBatchNo(),
                lot.getExpiryDate(),
                lot.getQuantity(),
                lot.getState().name());
    }
}
