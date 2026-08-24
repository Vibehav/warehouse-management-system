package com.project.wms.catalogue.dto;

import com.project.wms.catalogue.enums.RotationPolicy;
import com.project.wms.common.enums.ProductCategory;
import com.project.wms.common.enums.StorageZoneType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record CreateProductSkuRequestDto(
@NotNull Long supplierId,
@NotBlank String skuCode,
@NotBlank String name,
@NotNull ProductCategory category,
@NotNull StorageZoneType storageZoneType,
@NotNull RotationPolicy rotationPolicy,
@Positive Double weight,
@Positive Double length,
@Positive Double width,
@Positive Double height
) {}