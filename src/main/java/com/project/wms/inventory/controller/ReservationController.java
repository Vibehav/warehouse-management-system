package com.project.wms.inventory.controller;

import com.project.wms.inventory.dto.fulfillmentDTO.ReserveStockRequestDto;
import com.project.wms.inventory.service.FulfillmentReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final FulfillmentReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasAuthority('FULFILLMENT_RESERVE_ALLOCATE')")
    public ResponseEntity<FulfillmentReservationService.FulfillmentReservationResult> reserve(
            @Valid @RequestBody ReserveStockRequestDto request) {
        var result = reservationService.reserve(request.skuId(), request.warehouseId(), request.quantity());
        return ResponseEntity.ok(result);
    }
}
