package com.project.wms.catalogue.dto;

import com.project.wms.catalogue.enums.RotationPolicy;
import com.project.wms.common.enums.ProductCategory;
import com.project.wms.common.enums.StorageZoneType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateProductSkuRequestDto(
        @Pattern(regexp = ".*\\S.*", message = "must not be blank") @Size(max = 255) String name,
        ProductCategory category,
        StorageZoneType storageZoneType,
        RotationPolicy rotationPolicy,
        @Positive Double weight,
        @Positive Double length,
        @Positive Double width,
        @Positive Double height
) {
}
