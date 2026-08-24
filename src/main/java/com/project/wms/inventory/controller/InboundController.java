package com.project.wms.inventory.controller;

import com.project.wms.inventory.domain.InventoryLot;
import com.project.wms.inventory.dto.ConfirmPutawayRequestDto;
import com.project.wms.inventory.dto.InventoryLotResponseDto;
import com.project.wms.inventory.dto.PutawayResponseDto;
import com.project.wms.inventory.dto.ReceiveRequestDto;
import com.project.wms.inventory.facade.InboundPutawayFacade;
import com.project.wms.auth.service.WarehouseAccessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inbound")
@RequiredArgsConstructor
public class InboundController {

    private final InboundPutawayFacade putawayFacade;
    private final WarehouseAccessService warehouseAccessService;


    @PostMapping("/receive")
    @PreAuthorize("hasAuthority('INBOUND_RECEIVE')")
    public ResponseEntity<PutawayResponseDto> receive(
            Authentication authentication, @Valid @RequestBody ReceiveRequestDto request) {
        warehouseAccessService.assertCanAccess((Long) authentication.getPrincipal(), request.warehouseId());
        InboundPutawayFacade.PutawayResult result = putawayFacade.receive(
                request.skuId(), request.supplierId(), request.warehouseId(),
                request.quantity(), request.batchNo(), request.expiryDate()
        );
        return ResponseEntity.ok(PutawayResponseDto.from(result));
    }


    @PostMapping("/confirm")
    @PreAuthorize("hasAuthority('INBOUND_RECEIVE')")
    public ResponseEntity<InventoryLotResponseDto> confirm(@Valid @RequestBody ConfirmPutawayRequestDto request) {
        InventoryLot lot = putawayFacade.confirmPutaway(request.lotId(), request.scannedLocationCode());
        return ResponseEntity.ok(InventoryLotResponseDto.from(lot));
    }
}
