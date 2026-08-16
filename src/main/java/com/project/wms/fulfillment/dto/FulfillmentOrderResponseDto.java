package com.project.wms.fulfillment.dto;

import com.project.wms.fulfillment.domain.FulfillmentOrder;

import java.util.ArrayList;
import java.util.List;

public record FulfillmentOrderResponseDto(Long id, String status, Long warehouseId, List<LineDto> lines) {

    public record LineDto(String skuCode, int quantity) {}

    public static FulfillmentOrderResponseDto from(FulfillmentOrder order) {
        List<LineDto> lines = new ArrayList<>();
        for (var line : order.getLines()) {
            lines.add(new LineDto(line.getProductSku().getSkuCode(), line.getQuantity()));
        }
        return new FulfillmentOrderResponseDto(order.getId(), order.getStatus().name(), order.getWarehouse().getId(), lines);
    }
}
