package com.project.wms.inventory.dto;


import java.time.LocalDate;

public record ReceiveRequestDto(Long skuId,
                                Long supplierId,
                                Long warehouseId,
                                int quantity,
                                String batchNo,
                                LocalDate expiryDate) {
}
