package com.project.wms.warehouse.service.strategy;

import com.project.wms.catalogue.domain.ProductSku;
import com.project.wms.inventory.repository.InventoryBalanceRepository;
import com.project.wms.warehouse.domain.Location;
import com.project.wms.warehouse.domain.LocationAllocation;
import com.project.wms.warehouse.exception.InsufficientCapacityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("FIRST_AVAILABLE")
@RequiredArgsConstructor
public class FirstAvailableStrategy implements PlacementStrategy{


    private final InventoryBalanceRepository inventoryBalanceRepository;

    @Override
    public List<LocationAllocation> allocate(ProductSku sku, List<Location> eligibleLocations, int requiredQuantity) {
        List<LocationAllocation> plan = new ArrayList<>();
        int remaining = requiredQuantity;

        for(Location loc: eligibleLocations){
            if(remaining <= 0) break;
            int available = loc.getCapacity() - inventoryBalanceRepository.sumQuantityByLocationId(loc.getId());
            int take = Math.min(available, remaining);
            if (take > 0) {
                plan.add(new LocationAllocation(loc, take));
                remaining -= take;
            }
        }

        if (remaining > 0) {
            throw new InsufficientCapacityException("Sku Code: "+sku.getSkuCode());
        }
        return plan;
    }
}
