package com.project.wms.inventory.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.wms.auth.entity.User;
import com.project.wms.auth.exception.UserNotFoundException;
import com.project.wms.auth.repository.UserRepository;
import com.project.wms.inventory.domain.InventoryBalance;
import com.project.wms.inventory.domain.InventoryLot;
import com.project.wms.inventory.dto.InventoryBalanceResponseDto;
import com.project.wms.inventory.exception.InventoryBalanceNotFound;
import com.project.wms.inventory.repository.InventoryBalanceRepository;
import com.project.wms.supplier.exception.SupplierNotFoundException;
import com.project.wms.warehouse.domain.LocationAllocation;

import lombok.RequiredArgsConstructor;

 // "where and how much" side of a lot, as distinct from InventoryLot's "what".
@Service
@RequiredArgsConstructor
public class InventoryBalanceService {

    private final InventoryBalanceRepository balanceRepository;
    private final UserRepository userRepository;

    // for a given lot and for a given LOCATION Allocation plan, balance where inventory can be assigned are created.
     @Transactional
    public List<InventoryBalance> createBalances(InventoryLot lot, List<LocationAllocation> plan) {
        List<InventoryBalance> balances = new ArrayList<>();
        for (LocationAllocation allocation : plan) {
            InventoryBalance balance = InventoryBalance.of(lot, allocation.location(), allocation.quantity());
            balances.add(balance);
        }
        return balanceRepository.saveAll(balances);
    }

    // confirms part of lot balance has been placed at correct location. No lot Confirmation because lot might get split. Lot is confirmed at facade level.
    public InventoryBalance confirmBalance(Long balanceId) {
        InventoryBalance balance = balanceRepository.findById(balanceId).orElseThrow(() -> new InventoryBalanceNotFound("InventoryBalance not found: " + balanceId));
        balance.setConfirmed(true);
        return balanceRepository.save(balance);
    }

    // Just a check if every IB has been placed at the respective locations or not.
    public boolean isLotFullyConfirmed(Long lotId) {

        List<InventoryBalance> balances = balanceRepository.findByInventoryLotId(lotId);

        if (balances.isEmpty()) {
            return false;
        }

        for (InventoryBalance balance : balances) {
            if (!balance.isConfirmed()) {
                return false;
            }
        }
        return true;
    }

     public List<InventoryBalance> findByLotId(Long lotId) {
         List<InventoryBalance> balances =  balanceRepository.findByInventoryLotId(lotId);
         if(balances.isEmpty()) throw new InventoryBalanceNotFound("Inventory balance not found for the id" + lotId);
         return balances;
     }

     // used while inventory gets Fulfilled

     @Transactional
     public int reserve(Long balanceId, int requestedQty) {
         InventoryBalance balance = balanceRepository.findById(balanceId).orElseThrow(() -> new InventoryBalanceNotFound("InventoryBalance not found: " + balanceId));

         int available = balance.availableQuantity();
         int toReserve = Math.min(available, requestedQty);

         if (toReserve <= 0) {
             return 0;
         }

         balance.setReservedQuantity(balance.getReservedQuantity() + toReserve);
         balanceRepository.save(balance);

         return toReserve;
     }

     // Order Canceled, Reservation time-out
     @Transactional
     public void release(Long balanceId, int quantity) {
         InventoryBalance balance = balanceRepository.findById(balanceId)
                 .orElseThrow(() -> new InventoryBalanceNotFound("InventoryBalance not found: " + balanceId));

        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (quantity > balance.getReservedQuantity()) {
            throw new IllegalArgumentException("Cannot release more than the reserved quantity");
        }
        balance.setReservedQuantity(balance.getReservedQuantity() - quantity);
         int newReserved = balance.getReservedQuantity() - quantity;
         balance.setReservedQuantity(Math.max(newReserved, 0));
         balanceRepository.save(balance);
     }

     // Inventory View
     // ADMIN / WAREHOUSE_MANAGER / WAREHOUSE_STAFF — INVENTORY_VIEW
     public List<InventoryBalanceResponseDto> viewByWarehouse(Long warehouseId) {
         List<InventoryBalanceResponseDto> results = new ArrayList<>();
         for (InventoryBalance balance : balanceRepository.findByWarehouseId(warehouseId)) {
             results.add(InventoryBalanceResponseDto.from(balance));
         }
         return results;
     }


     public List<InventoryBalanceResponseDto> viewOwn(Long warehouseId,Long userId) {
         User user = userRepository.findById(userId)
                 .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

         if (user.getSupplier() == null) {
             throw new SupplierNotFoundException("User " + userId + " has no linked supplier");
         }

         List<InventoryBalanceResponseDto> results = new ArrayList<>();
         for (InventoryBalance balance : balanceRepository.findBySupplierId(warehouseId,user.getSupplier().getId())) {
             results.add(InventoryBalanceResponseDto.from(balance));
         }
         return results;
     }

 }