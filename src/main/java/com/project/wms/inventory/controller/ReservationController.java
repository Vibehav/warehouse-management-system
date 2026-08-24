package com.project.wms.inventory.controller;

import com.project.wms.inventory.dto.fulfillmentDTO.ReserveStockRequestDto;
import com.project.wms.inventory.service.FulfillmentReservationService;
import com.project.wms.auth.service.WarehouseAccessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final FulfillmentReservationService reservationService;
    private final WarehouseAccessService warehouseAccessService;

    @PostMapping
    @PreAuthorize("hasAuthority('FULFILLMENT_RESERVE_ALLOCATE')")
    public ResponseEntity<FulfillmentReservationService.FulfillmentReservationResult> reserve(
            Authentication authentication, @Valid @RequestBody ReserveStockRequestDto request) {
        warehouseAccessService.assertCanAccess((Long) authentication.getPrincipal(), request.warehouseId());
        var result = reservationService.reserve(request.skuId(), request.warehouseId(), request.quantity());
        return ResponseEntity.ok(result);
    }
}
