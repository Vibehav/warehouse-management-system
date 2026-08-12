package com.project.wms.warehouse.service.strategy;

import com.project.wms.catalogue.domain.ProductSku;
import com.project.wms.warehouse.domain.Location;
import com.project.wms.warehouse.domain.LocationAllocation;

import java.util.List;

public interface PlacementStrategy {
    List<LocationAllocation> allocate(ProductSku sku, List<Location> eligibleLocations, int requiredQuantity);
}
