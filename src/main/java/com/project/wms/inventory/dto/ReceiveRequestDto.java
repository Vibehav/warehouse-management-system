package com.project.wms.inventory.dto;


import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReceiveRequestDto(@NotNull Long skuId,
                                @NotNull Long supplierId,
                                @NotNull Long warehouseId,
                                @Positive int quantity,
                                @NotBlank String batchNo,
                                LocalDate expiryDate) {
}
