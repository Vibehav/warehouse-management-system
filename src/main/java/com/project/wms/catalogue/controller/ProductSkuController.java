package com.project.wms.catalogue.controller;

import com.project.wms.catalogue.dto.CreateProductSkuRequestDto;
import com.project.wms.catalogue.dto.ProductSkuResponseDto;
import com.project.wms.catalogue.dto.UpdateProductSkuRequestDto;
import com.project.wms.catalogue.service.ProductSkuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-skus")
@RequiredArgsConstructor
public class ProductSkuController {

    private final ProductSkuService productSkuService;

    @PostMapping
    @PreAuthorize("hasAuthority('CATALOGUE_MANAGE')")
    public ResponseEntity<ProductSkuResponseDto> create(@Valid @RequestBody CreateProductSkuRequestDto request) {
        ProductSkuResponseDto response = productSkuService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CATALOGUE_VIEW')")
    public ResponseEntity<ProductSkuResponseDto> getById(@PathVariable Long id) {

        return ResponseEntity.ok(productSkuService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CATALOGUE_VIEW')")
    public ResponseEntity<List<ProductSkuResponseDto>> getAll() {
        return ResponseEntity.ok(productSkuService.getAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CATALOGUE_MANAGE')")
    public ResponseEntity<ProductSkuResponseDto> update(@PathVariable Long id, @Valid @RequestBody UpdateProductSkuRequestDto request) {
        return ResponseEntity.ok(productSkuService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CATALOGUE_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productSkuService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasAuthority('CATALOGUE_MANAGE')")
    public ResponseEntity<ProductSkuResponseDto> restore(@PathVariable Long id) {
        return ResponseEntity.ok(productSkuService.restore(id));
    }
}
