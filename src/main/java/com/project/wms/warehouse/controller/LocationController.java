package com.project.wms.warehouse.controller;

import com.project.wms.warehouse.dto.locationDTO.CreateLocationRequestDto;
import com.project.wms.warehouse.dto.locationDTO.LocationResponseDto;
import com.project.wms.warehouse.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses/{warehouseId}/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    @PreAuthorize("hasAuthority('LOCATION_MANAGE')")
    public ResponseEntity<LocationResponseDto> create(@PathVariable Long warehouseId, @RequestBody CreateLocationRequestDto request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(locationService.create(warehouseId, request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LOCATION_VIEW')")
    public ResponseEntity<List<LocationResponseDto>> getAll(@PathVariable Long warehouseId) {

        return ResponseEntity.ok(locationService.getAll(warehouseId));
    }

    @GetMapping("/{locationId}")
    @PreAuthorize("hasAuthority('LOCATION_VIEW')")
    public ResponseEntity<LocationResponseDto> getById(@PathVariable Long warehouseId, @PathVariable Long locationId) {

        return ResponseEntity.ok(locationService.getById(warehouseId,locationId));
    }

    @PatchMapping("/{locationId}/activate")
    @PreAuthorize("hasAuthority('LOCATION_MANAGE')")
    public ResponseEntity<Void> activate(@PathVariable Long warehouseId, @PathVariable Long locationId) {

        locationService.activate(warehouseId,locationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{locationId}/deactivate")
    @PreAuthorize("hasAuthority('LOCATION_MANAGE')")
    public ResponseEntity<Void> deactivate(@PathVariable Long warehouseId, @PathVariable Long locationId) {
        locationService.deactivate(warehouseId,locationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{locationId}/block")
    @PreAuthorize("hasAuthority('LOCATION_MANAGE')")
    public ResponseEntity<Void> block(@PathVariable Long warehouseId, @PathVariable Long locationId) {
        locationService.block(warehouseId,locationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{locationId}/unblock")
    @PreAuthorize("hasAuthority('LOCATION_MANAGE')")
    public ResponseEntity<Void> unblock(@PathVariable Long warehouseId, @PathVariable Long locationId) {
        locationService.unblock(warehouseId,locationId);

        return ResponseEntity.noContent().build();
    }
}