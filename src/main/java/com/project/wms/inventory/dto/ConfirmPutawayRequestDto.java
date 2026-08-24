package com.project.wms.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConfirmPutawayRequestDto(@NotNull Long lotId, @NotBlank String scannedLocationCode) {
}
