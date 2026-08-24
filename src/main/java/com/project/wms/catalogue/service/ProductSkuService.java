package com.project.wms.catalogue.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.wms.catalogue.domain.ProductSku;
import com.project.wms.catalogue.dto.CreateProductSkuRequestDto;
import com.project.wms.catalogue.dto.ProductSkuResponseDto;
import com.project.wms.catalogue.dto.UpdateProductSkuRequestDto;
import com.project.wms.catalogue.exception.ProductSkuCodeAlreadyExistsException;
import com.project.wms.catalogue.exception.ProductSkuNotFoundException;
import com.project.wms.catalogue.repository.ProductSkuRepository;
import com.project.wms.supplier.domain.Supplier;
import com.project.wms.supplier.exception.SupplierNotFoundException;
import com.project.wms.supplier.repository.SupplierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductSkuService {

    private final ProductSkuRepository skuRepository;
    private final SupplierRepository supplierRepository;

    @Transactional
    public ProductSkuResponseDto create(CreateProductSkuRequestDto request) {

        if (skuRepository.existsBySkuCode(request.skuCode())) {
            throw new ProductSkuCodeAlreadyExistsException("SKU code already exists: " + request.skuCode());
        }

        Supplier supplier = supplierRepository.findByIdAndDeletedFalse(request.supplierId())
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found: " + request.supplierId() + " check if it has been deleted."));

        ProductSku sku = new ProductSku();
        sku.setSupplier(supplier);
        sku.setSkuCode(request.skuCode());
        sku.setName(request.name());
        sku.setCategory(request.category());
        sku.setStorageZoneType(request.storageZoneType());
        sku.setRotationPolicy(request.rotationPolicy());
        sku.setWeight(request.weight());
        sku.setLength(request.length());
        sku.setWidth(request.width());
        sku.setHeight(request.height());

        ProductSku savedSku = skuRepository.save(sku);

        return ProductSkuResponseDto.from(savedSku);
    }

    public ProductSkuResponseDto getById(Long id) {
        ProductSku sku = skuRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ProductSkuNotFoundException("Product SKU not found: " + id + " check if it has been deleted."));
        return ProductSkuResponseDto.from(sku);
    }

    public List<ProductSkuResponseDto> getAll() {
        return skuRepository.findAllByDeletedFalse()
                .stream()
                .map(ProductSkuResponseDto::from)
                .toList();
    }

    @Transactional
    public ProductSkuResponseDto update(Long id, UpdateProductSkuRequestDto request) {

        ProductSku sku = skuRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ProductSkuNotFoundException("Product SKU not found: " + id + " check if it has been deleted."));

        if (request.name() != null && !request.name().isBlank()) sku.setName(request.name());
        if (request.category() != null) sku.setCategory(request.category());
        if (request.storageZoneType() != null) sku.setStorageZoneType(request.storageZoneType());
        if (request.rotationPolicy() != null) sku.setRotationPolicy(request.rotationPolicy());
        if (request.weight() != null && request.weight() > 0) sku.setWeight(request.weight());
        if (request.length() != null && request.length() > 0) sku.setLength(request.length());
        if (request.width() != null && request.width() > 0) sku.setWidth(request.width());
        if (request.height() != null && request.height() > 0) sku.setHeight(request.height());

        return ProductSkuResponseDto.from(sku);
    }

    @Transactional
    public void delete(Long id) {
        ProductSku sku = skuRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ProductSkuNotFoundException("Product SKU not found: " + id));

        sku.setDeleted(true);
    }

    @Transactional
    public ProductSkuResponseDto restore(Long id) {
        ProductSku sku = skuRepository.findByIdAndDeletedTrue(id)
                .orElseThrow(() -> new ProductSkuNotFoundException("Deleted Product SKU not found: " + id));
        sku.setDeleted(false);
        return ProductSkuResponseDto.from(sku);
    }
}
