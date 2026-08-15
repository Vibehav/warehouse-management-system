package com.project.wms.inventory.repository;

import com.project.wms.inventory.domain.InventoryLot;
import com.project.wms.inventory.enums.LotState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InventoryLotRepository extends JpaRepository<InventoryLot, Long> {

    // Hot-path query: "show AVAILABLE stock in warehouse X" -
    // matches the composite (warehouse_id, state) index from the ERD.
    List<InventoryLot> findByWarehouseIdAndState(Long warehouseId, LotState state);

    // Tenant-scoped query for the Supplier role.
    List<InventoryLot> findBySupplierId(Long supplierId);

    List<InventoryLot> findByStateAndExpiryDateLessThanEqual(LotState state, LocalDate date);
}
