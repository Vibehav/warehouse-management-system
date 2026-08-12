package com.project.wms.warehouse.service;

import com.project.wms.catalogue.domain.ProductSku;
import com.project.wms.common.enums.StorageZoneType;
import com.project.wms.inventory.repository.InventoryBalanceRepository;
import com.project.wms.warehouse.domain.Location;
import com.project.wms.warehouse.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CandidateLocationService {

    private final LocationRepository locationRepository;
    private final InventoryBalanceRepository balanceRepository;

    public List<Location> findEligibleLocations(Long warehouseId, ProductSku sku) {

        List<Location> locations = locationRepository.findByWarehouseIdAndStorageZoneTypeAndActiveTrueAndBlockedFalse(warehouseId,sku.getStorageZoneType());
        List<Location> availableLocations = new ArrayList<>();
        for(Location loc: locations) {
            if(loc.isActive() && !loc.isBlocked()) {
               int remainingCapacity = 0;

               Integer usedQuantity = balanceRepository.sumQuantityByLocationId(loc.getId());
               remainingCapacity = loc.getCapacity() - usedQuantity;

               if(remainingCapacity > 0) availableLocations.add(loc);

            }

        }
        /*
        * 1. Fetch ALL LOCATIONS + Filter ( SKU storage type == Location storage type, Active and Unblocked)
        * 2. Remaining Capacity ( total location Capacity - usedQuantity)
        * 3. if (remaining capacity > 0 ) add to availableLocations
        * */

    return availableLocations;
    }

}
