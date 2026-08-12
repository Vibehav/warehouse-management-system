package com.project.wms.catalogue.dto;

import com.project.wms.common.enums.ProductCategory;
import com.project.wms.common.enums.StorageZoneType;

public record CreateProductSkuRequestDto(
        Long supplierId,
        String skuCode,
        String name,
        ProductCategory category,
        StorageZoneType storageZoneType,
        Double weight, Double length, Double width, Double height
) {}