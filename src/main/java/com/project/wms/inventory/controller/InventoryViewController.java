package com.project.wms.inventory.controller;

import com.project.wms.inventory.dto.InventoryBalanceResponseDto;
import com.project.wms.inventory.dto.SupplierWarehouseInventoryResponseDto;
import com.project.wms.auth.service.WarehouseAccessService;
import com.project.wms.inventory.service.InventoryBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryViewController {

    private final InventoryBalanceService inventoryViewService;
    private final WarehouseAccessService warehouseAccessService;

    /** ADMIN / WAREHOUSE_MANAGER / WAREHOUSE_STAFF — view all stock in a warehouse */
    @GetMapping
    @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
    public ResponseEntity<List<InventoryBalanceResponseDto>> viewByWarehouse(
            @RequestParam Long warehouseId, Authentication authentication) {
        warehouseAccessService.assertCanAccess((Long) authentication.getPrincipal(), warehouseId);
        return ResponseEntity.ok(inventoryViewService.viewByWarehouse(warehouseId));
    }

    /** SUPPLIER — view only their own stock, across all warehouses */
    @GetMapping("/mine")
    @PreAuthorize("hasAuthority('INVENTORY_VIEW_OWN')")
    public ResponseEntity<List<SupplierWarehouseInventoryResponseDto>> viewOwn(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(inventoryViewService.viewOwnAcrossWarehouses(userId));
    }
}
