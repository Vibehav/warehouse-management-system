package com.project.wms.inventory.dto.fulfillmentDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReserveStockRequestDto(@NotNull Long skuId, @NotNull Long warehouseId, @Positive int quantity) {}
