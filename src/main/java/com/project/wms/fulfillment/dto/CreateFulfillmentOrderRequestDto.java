package com.project.wms.fulfillment.dto;

import java.util.List;

public record CreateFulfillmentOrderRequestDto(Long warehouseId, List<OrderLineRequestDto> lines) {}
