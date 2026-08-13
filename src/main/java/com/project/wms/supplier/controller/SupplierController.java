package com.project.wms.supplier.controller;

import com.project.wms.supplier.dto.CreateSupplierRequestDto;
import com.project.wms.supplier.dto.SupplierResponseDto;
import com.project.wms.supplier.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @PreAuthorize("hasAuthority('SUPPLIER_ONBOARD')")
    public ResponseEntity<SupplierResponseDto> create(@RequestBody CreateSupplierRequestDto request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SUPPLIER_VIEW')")
    public ResponseEntity<List<SupplierResponseDto>> getAll() {
        return ResponseEntity.ok(supplierService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPPLIER_VIEW')")
    public ResponseEntity<SupplierResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getById(id));}

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPPLIER_ONBOARD')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasAuthority('SUPPLIER_ONBOARD')")
    public ResponseEntity<SupplierResponseDto> restore(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.restore(id));
    }
}