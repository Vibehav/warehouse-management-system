package com.project.wms.inventory.controller;

import com.project.wms.inventory.dto.fulfillmentDTO.ReleaseStockRequestDto;
import com.project.wms.inventory.dto.fulfillmentDTO.ReserveStockRequestDto;
import com.project.wms.inventory.service.FulfillmentReservationService;
import com.project.wms.inventory.service.InventoryBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final FulfillmentReservationService reservationService;
    private final InventoryBalanceService balanceService;

    @PostMapping
    @PreAuthorize("hasAuthority('FULFILLMENT_RESERVE_ALLOCATE')")
    public ResponseEntity<FulfillmentReservationService.FulfillmentReservationResult> reserve(
            @RequestBody ReserveStockRequestDto request) {
        var result = reservationService.reserve(request.skuId(), request.warehouseId(), request.quantity());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/release")
    @PreAuthorize("hasAuthority('FULFILLMENT_RESERVE_ALLOCATE')")
    public ResponseEntity<Void> release(@RequestBody ReleaseStockRequestDto request) {
        balanceService.release(request.balanceId(), request.quantity());
        return ResponseEntity.noContent().build();
    }
}