package com.project.wms.warehouse.service;

import com.project.wms.warehouse.domain.Warehouse;
import com.project.wms.warehouse.dto.warehouseDTO.CreateWarehouseRequestDto;
import com.project.wms.warehouse.dto.warehouseDTO.WarehouseResponseDto;
import com.project.wms.warehouse.exception.WarehouseNotFoundException;
import com.project.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Transactional
    public WarehouseResponseDto create(CreateWarehouseRequestDto request) {

        if (warehouseRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Warehouse code already exists: " + request.code());
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.name());
        warehouse.setCode(request.code());
        warehouse.setPlacementStrategyType(request.placementStrategyType());
        warehouse.setActive(true);

        Warehouse saved = warehouseRepository.save(warehouse);
        return WarehouseResponseDto.from(saved);
    }

    public List<WarehouseResponseDto> getAll() {

        return warehouseRepository.findAllByActiveTrue()
                .stream()
                .map(WarehouseResponseDto::from)
                .toList();
    }

    public WarehouseResponseDto getById(Long id) {

        Warehouse warehouse = warehouseRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found: " + id));

        return WarehouseResponseDto.from(warehouse);
    }

    @Transactional
    public WarehouseResponseDto update(Long id, CreateWarehouseRequestDto request) {
        Warehouse warehouse = warehouseRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found: " + id));

        warehouse.setName(request.name());
        warehouse.setPlacementStrategyType(request.placementStrategyType());

        return WarehouseResponseDto.from(warehouse);
    }

    @Transactional
    public void deactivate(Long id) {
        Warehouse warehouse = warehouseRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found: " + id));

        warehouse.setActive(false);
    }

    @Transactional
    public WarehouseResponseDto activate(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found: " + id));

        warehouse.setActive(true);
        return WarehouseResponseDto.from(warehouse);
    }
}