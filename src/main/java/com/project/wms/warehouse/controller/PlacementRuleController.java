package com.project.wms.warehouse.controller;

import com.project.wms.warehouse.dto.placementDTO.CreatePlacementRuleRequestDto;
import com.project.wms.warehouse.dto.placementDTO.PlacementRuleResponseDto;
import com.project.wms.warehouse.service.PlacementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/placement-rules")
@RequiredArgsConstructor
public class PlacementRuleController {

    private final PlacementService placementService;

    @PostMapping
    @PreAuthorize("hasAuthority('WAREHOUSE_CONFIG_MANAGE')")
    public ResponseEntity<PlacementRuleResponseDto> create(@RequestBody CreatePlacementRuleRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(placementService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('WAREHOUSE_CONFIG_MANAGE')")
    public ResponseEntity<List<PlacementRuleResponseDto>> getAll() {
        return ResponseEntity.ok(placementService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('WAREHOUSE_CONFIG_MANAGE')")
    public ResponseEntity<PlacementRuleResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(placementService.getById(id));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('WAREHOUSE_CONFIG_MANAGE')")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        placementService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('WAREHOUSE_CONFIG_MANAGE')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        placementService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}