package com.project.wms.inventory.repository;

import com.project.wms.inventory.domain.InventoryBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InventoryBalanceRepository extends JpaRepository<InventoryBalance, Long> {

    List<InventoryBalance> findByInventoryLotId(Long lotId);

    @Query("""
        SELECT ib FROM InventoryBalance ib JOIN ib.inventoryLot il
        WHERE il.productSku.id = :skuId
          AND il.warehouse.id = :warehouseId
          AND (ib.quantity - ib.reservedQuantity) > 0
          AND il.state = 'AVAILABLE'
        """)
    List<InventoryBalance> findReservableBalancesForSku(Long skuId, Long warehouseId);

    // InventoryBalanceRepository.java — add this
    @Query("SELECT COALESCE(SUM(ib.quantity), 0) FROM InventoryBalance ib WHERE ib.location.id = :locationId")
    Integer sumQuantityByLocationId(Long locationId);

    @Query("""
    SELECT ib FROM InventoryBalance ib JOIN ib.inventoryLot il WHERE il.warehouse.id = :warehouseId""")
    List<InventoryBalance> findByWarehouseId(Long warehouseId);

    /** SUPPLIER — only their own stock, across every warehouse. */
    @Query("""
    SELECT ib FROM InventoryBalance ib
    JOIN FETCH ib.inventoryLot il
    JOIN FETCH il.productSku
    JOIN FETCH il.supplier
    JOIN FETCH il.warehouse
    JOIN FETCH ib.location
    WHERE il.supplier.id = :supplierId
        """)
    List<InventoryBalance> findAllBySupplierId(Long supplierId);
}
