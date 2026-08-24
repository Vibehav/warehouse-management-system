package com.project.wms.inventory.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.wms.catalogue.domain.ProductSku;
import com.project.wms.catalogue.enums.RotationPolicy;
import com.project.wms.catalogue.exception.ProductSkuNotFoundException;
import com.project.wms.catalogue.repository.ProductSkuRepository;
import com.project.wms.inventory.domain.InventoryBalance;
import com.project.wms.inventory.exception.InsufficientStockException;
import com.project.wms.inventory.repository.InventoryBalanceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FulfillmentReservationService {

    private final InventoryBalanceRepository balanceRepository;
    private final ProductSkuRepository skuRepository;
    private final InventoryBalanceService balanceService;

    @Transactional
    public FulfillmentReservationResult reserve(Long skuId, Long warehouseId, int requestedQty) {
        ProductSku sku = skuRepository.findByIdAndDeletedFalse(skuId)
                .orElseThrow(() -> new ProductSkuNotFoundException("SKU not found: " + skuId));

        List<InventoryBalance> candidates = balanceRepository.findReservableBalancesForSku(skuId, warehouseId);
        sortByRotationPolicy(candidates, sku.getRotationPolicy());

        List<ReservationLine> lines = new ArrayList<>();
        int remaining = requestedQty;

        for (InventoryBalance balance : candidates) {
            if (remaining <= 0) {
                break;
            }
            int reservedFromThisBalance = balanceService.reserve(balance.getId(), remaining);
            if (reservedFromThisBalance > 0) {
                lines.add(new ReservationLine(balance.getId(), reservedFromThisBalance));
                remaining -= reservedFromThisBalance;
            }
        }

        if (remaining > 0) {
            // @Transactional rolls back every reserve() made in this loop —
            // an order is either FULLY reservable or not reserved at all.
            throw new InsufficientStockException(sku.getSkuCode(), requestedQty, requestedQty - remaining);
        }

        return new FulfillmentReservationResult(skuId, requestedQty, lines);
    }

    // FEFO,FIFO used for picking
    private void sortByRotationPolicy(List<InventoryBalance> balances, RotationPolicy policy) {
        Comparator<InventoryBalance> comparator;
        if (policy == RotationPolicy.FEFO) {
            comparator = Comparator.comparing(
                    b -> b.getInventoryLot().getExpiryDate(),
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
        } else {
            comparator = Comparator.comparing(b -> b.getInventoryLot().getCreatedAt());
        }
        balances.sort(comparator);
    }

    public record ReservationLine(Long balanceId, int quantity) {}

    public record FulfillmentReservationResult(Long skuId, int requestedQty, List<ReservationLine> lines) {}
}