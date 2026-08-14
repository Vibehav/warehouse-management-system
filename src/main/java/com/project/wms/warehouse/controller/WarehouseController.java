package com.project.wms.warehouse.controller;

import com.project.wms.warehouse.dto.warehouseDTO.CreateWarehouseRequestDto;
import com.project.wms.warehouse.dto.warehouseDTO.WarehouseResponseDto;
import com.project.wms.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @PreAuthorize("hasAuthority('WAREHOUSE_CONFIG_MANAGE')")
    public ResponseEntity<WarehouseResponseDto> create(@RequestBody CreateWarehouseRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouseService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('WAREHOUSE_VIEW')")
    public ResponseEntity<List<WarehouseResponseDto>> getAll() {
        return ResponseEntity.ok(warehouseService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('WAREHOUSE_VIEW')")
    public ResponseEntity<WarehouseResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WAREHOUSE_CONFIG_MANAGE')")
    public ResponseEntity<WarehouseResponseDto> update(@PathVariable Long id, @RequestBody CreateWarehouseRequestDto request) {
        return ResponseEntity.ok(warehouseService.update(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('WAREHOUSE_CONFIG_MANAGE')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        warehouseService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('WAREHOUSE_CONFIG_MANAGE')")
    public ResponseEntity<WarehouseResponseDto> activate(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.activate(id));
    }
}