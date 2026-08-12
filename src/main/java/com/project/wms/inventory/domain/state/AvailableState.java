package com.project.wms.inventory.domain.state;

import com.project.wms.inventory.domain.InventoryLot;
import com.project.wms.inventory.enums.LotState;

public class AvailableState implements InventoryLotState {

    @Override
    public void markShipped(InventoryLot lot) {
        lot.transitionTo(LotState.SHIPPED);
    }

    @Override
    public void expire(InventoryLot lot) {
        lot.transitionTo(LotState.EXPIRED);
    }

    @Override
    public void markDamaged(InventoryLot lot) {
        lot.transitionTo(LotState.DAMAGED);
    }

}