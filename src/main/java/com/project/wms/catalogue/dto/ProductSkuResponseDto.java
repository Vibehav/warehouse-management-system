package com.project.wms.catalogue.dto;

import com.project.wms.catalogue.domain.ProductSku;

public record ProductSkuResponseDto(Long id, String skuCode, String name, Long supplierId) {

    public static ProductSkuResponseDto from(ProductSku sku) {
        return new ProductSkuResponseDto(
                sku.getId(),
                sku.getSkuCode(),
                sku.getName(),
                sku.getSupplier().getId());
    }
}