package com.project.wms.catalogue.dto;

import com.project.wms.catalogue.domain.ProductSku;
import com.project.wms.catalogue.enums.RotationPolicy;
import com.project.wms.common.enums.ProductCategory;
import com.project.wms.common.enums.StorageZoneType;

public record ProductSkuResponseDto(Long id, String skuCode, String name, Long supplierId,
                                    ProductCategory category, StorageZoneType storageZoneType,
                                    RotationPolicy rotationPolicy, Double weight, Double length,
                                    Double width, Double height) {

    public static ProductSkuResponseDto from(ProductSku sku) {
        return new ProductSkuResponseDto(
                sku.getId(),
                sku.getSkuCode(),
                sku.getName(),
                sku.getSupplier().getId(),
                sku.getCategory(), sku.getStorageZoneType(), sku.getRotationPolicy(),
                sku.getWeight(), sku.getLength(), sku.getWidth(), sku.getHeight());
    }
}
