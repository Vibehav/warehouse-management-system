package com.project.wms.inventory.dto.fulfillmentDTO;

public record ReserveStockRequestDto(Long skuId, Long warehouseId, int quantity) {}
