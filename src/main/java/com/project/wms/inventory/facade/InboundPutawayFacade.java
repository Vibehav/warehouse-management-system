package com.project.wms.inventory.facade;

import com.project.wms.catalogue.domain.ProductSku;
import com.project.wms.catalogue.exception.ProductSkuNotFoundException;
import com.project.wms.catalogue.repository.ProductSkuRepository;
import com.project.wms.inventory.domain.InventoryBalance;
import com.project.wms.inventory.domain.InventoryLot;
import com.project.wms.inventory.domain.state.InventoryLotStateResolver;
import com.project.wms.inventory.repository.InventoryLotRepository;
import com.project.wms.inventory.service.InventoryBalanceService;
import com.project.wms.supplier.domain.Supplier;
import com.project.wms.supplier.exception.SupplierNotFoundException;
import com.project.wms.supplier.repository.SupplierRepository;
import com.project.wms.warehouse.domain.LocationAllocation;
import com.project.wms.warehouse.domain.Warehouse;
import com.project.wms.warehouse.exception.WarehouseNotFoundException;
import com.project.wms.warehouse.repository.WarehouseRepository;
import com.project.wms.warehouse.service.PlacementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InboundPutawayFacade {

    private final ProductSkuRepository skuRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryLotRepository lotRepository;
    private final PlacementService placementService;
    private final InventoryBalanceService balanceService;
    private final InventoryLotStateResolver stateResolver;


    @Transactional
    public PutawayResult receive(Long skuId, Long supplierId, Long warehouseId,
                                 int quantity, String batchNo, LocalDate expiryDate) {

        ProductSku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new ProductSkuNotFoundException("SKU not found: " + skuId));
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found: " + supplierId));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found: " + warehouseId));

        // Step 1 — Create InventoryLot
        InventoryLot lot = InventoryLot.builder()
                .productSku(sku)
                .supplier(supplier)
                .warehouse(warehouse)
                .batchNo(batchNo)
                .expiryDate(expiryDate)
                .quantity(quantity)
                .build();
        lot = lotRepository.save(lot);

        // Steps 2-4 — eligible locations, resolve rule, run strategy.
        List<LocationAllocation> plan = placementService.decidePlacement(warehouseId, sku, quantity);

        // Step 5 — Create InventoryBalance row(s), one per allocation
        List<InventoryBalance> balances = balanceService.createBalances(lot, plan);

        // Step 6 — return what the scanner should display; it decides nothing
        return new PutawayResult(lot.getId(), balances);
    }


    @Transactional
    public InventoryLot confirmPutaway(Long lotId,String scannedLocationCode){
        List<InventoryBalance> balances = balanceService.findByLotId(lotId);

        InventoryBalance matching = balances.stream()
                .filter(b -> b.getLocation().getCode().equals(scannedLocationCode))
                .findFirst()
                .orElseThrow(()-> new IllegalStateException("Scanned location "+scannedLocationCode+" does not math any assignment"));

        balanceService.confirmBalance(matching.getId());

        InventoryLot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new IllegalStateException("InventoryLot not found: " + lotId));

        if (balanceService.isLotFullyConfirmed(lotId)) {
            stateResolver.resolve(lot).markAvailable(lot);
            lotRepository.save(lot);
        }
        // else: other balances for this lot are still unconfirmed -> lot stays RECEIVED

        return lot;
    }

    public record PutawayResult(Long lotId, List<InventoryBalance> balances) {}
}