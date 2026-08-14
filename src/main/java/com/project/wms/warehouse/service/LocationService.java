package com.project.wms.warehouse.service;

import com.project.wms.warehouse.domain.Location;
import com.project.wms.warehouse.domain.Warehouse;
import com.project.wms.warehouse.dto.locationDTO.CreateLocationRequestDto;
import com.project.wms.warehouse.dto.locationDTO.LocationResponseDto;
import com.project.wms.warehouse.exception.LocationNotFoundException;
import com.project.wms.warehouse.exception.WarehouseNotFoundException;
import com.project.wms.warehouse.repository.LocationRepository;
import com.project.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final WarehouseRepository warehouseRepository;

    @Transactional
    public LocationResponseDto create(Long warehouseId, CreateLocationRequestDto request) {

        Warehouse warehouse = warehouseRepository.findByIdAndActiveTrue(warehouseId).orElseThrow(() ->
                        new WarehouseNotFoundException("Active warehouse not found: " + warehouseId));

        if (locationRepository.existsByWarehouseIdAndCode(warehouseId, request.code())) {
            throw new IllegalArgumentException("Location code already exists in warehouse: " + request.code());
        }

        Location location = new Location();

        location.setWarehouse(warehouse);
        location.setCode(request.code());
        location.setCapacity(request.capacity());
        location.setStorageZoneType(request.storageZoneType());
        location.setSequenceOrder(request.sequenceOrder());
        location.setActive(true);
        location.setBlocked(false);

        Location saved = locationRepository.save(location);

        return LocationResponseDto.from(saved);
    }

    public List<LocationResponseDto> getAll(Long warehouseId) {

        return locationRepository
                .findByWarehouseIdAndActiveTrue(warehouseId)
                .stream()
                .map(LocationResponseDto::from)
                .toList();
    }

    public LocationResponseDto getById(Long warehouseId,Long locationId) {
        Location location = locationRepository.findActiveLocationByIdAndWarehouseId(locationId,warehouseId).orElseThrow(() -> new LocationNotFoundException("Location not found: " + locationId));
        return LocationResponseDto.from(location);

    }

    @Transactional
    public void deactivate(Long warehouseId,Long locationId) {
        Location location = locationRepository.findActiveLocationByIdAndWarehouseId(locationId,warehouseId).orElseThrow(() -> new LocationNotFoundException("Location not found: " + locationId));

        location.setActive(false);
    }

    @Transactional
    public void activate(Long warehouseId,Long locationId) {
        Location location = locationRepository.findInActiveLocationByIdAndWarehouseId(locationId,warehouseId).orElseThrow(() -> new LocationNotFoundException("Location not found: " + locationId));

        if (!location.getWarehouse().isActive()) {
            throw new IllegalStateException("Cannot activate location because its warehouse is inactive");
        }

        location.setActive(true);
    }

    @Transactional
    public void block(Long warehouseId,Long locationId) {
        Location location = locationRepository.findUnblockedLocationByIdAndWarehouseId(locationId,warehouseId).orElseThrow(() -> new LocationNotFoundException("Location not found: " + locationId));
        location.setBlocked(true);
    }

    @Transactional
    public void unblock(Long warehouseId,Long locationId) {
        Location location = locationRepository.findBlockedLocationByIdAndWarehouseId(warehouseId,locationId).orElseThrow(() -> new LocationNotFoundException("Location not found: " + locationId));
        location.setBlocked(false);
    }
}