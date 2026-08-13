package com.project.wms.supplier.service;

import com.project.wms.supplier.domain.Supplier;
import com.project.wms.supplier.dto.CreateSupplierRequestDto;
import com.project.wms.supplier.dto.SupplierResponseDto;
import com.project.wms.supplier.exception.SupplierNotFoundException;
import com.project.wms.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Transactional
    public SupplierResponseDto create(CreateSupplierRequestDto request) {

        if (supplierRepository.existsByCodeAndDeletedFalse(request.code())) {
            throw new IllegalArgumentException("Supplier code already exists: " + request.code());
        }

        Supplier supplier = new Supplier();
        supplier.setName(request.name());
        supplier.setCode(request.code());
        supplier.setDeleted(false);

        Supplier savedSupplier = supplierRepository.save(supplier);

        return toResponse(savedSupplier);
    }

    public SupplierResponseDto getById(Long id) {
        Supplier supplier = supplierRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new SupplierNotFoundException("Supplier not found: " + id));

        return toResponse(supplier);
    }

    public List<SupplierResponseDto> getAll() {
        return supplierRepository.findAllByDeletedFalse()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        Supplier supplier = supplierRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new SupplierNotFoundException("Supplier not found: " + id));

        supplier.setDeleted(true);
    }

    @Transactional
    public SupplierResponseDto restore(Long id) {
        Supplier supplier = supplierRepository.findByIdAndDeletedTrue(id).orElseThrow(() -> new SupplierNotFoundException("Deleted supplier not found: " + id));

        supplier.setDeleted(false);
        return toResponse(supplier);
    }

    private SupplierResponseDto toResponse(Supplier supplier) {

        return new SupplierResponseDto(
                supplier.getId(),
                supplier.getName(),
                supplier.getCode()
        );
    }

    public Supplier getByActiveId(Long id) {
        return supplierRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new SupplierNotFoundException("Supplier not found: " + id));
    }
}