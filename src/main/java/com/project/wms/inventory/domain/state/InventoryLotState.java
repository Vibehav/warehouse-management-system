package com.project.wms.inventory.domain.state;


import com.project.wms.inventory.domain.InventoryLot;

public interface InventoryLotState {

    default void markAvailable(InventoryLot lot){
        throw illegal(lot, "markAvailable");
    }
    default void markShipped(InventoryLot lot){
        throw illegal(lot, "markShipped");
    }
    default void markDamaged(InventoryLot lot){
        throw illegal(lot, "markDamaged");
    }
    default void expire(InventoryLot lot){
        throw illegal(lot, "expire");
    }

    private IllegalStateException illegal(InventoryLot lot, String action) {
        return new IllegalStateException(
                "Cannot " + action + " lot " + lot.getId() + " from state " + lot.getState());
    }


}
