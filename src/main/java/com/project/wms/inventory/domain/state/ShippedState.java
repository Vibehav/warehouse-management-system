package com.project.wms.inventory.domain.state;

import com.project.wms.inventory.domain.InventoryLot;
import com.project.wms.inventory.enums.LotState;

public class ShippedState implements InventoryLotState{
    @Override
    public void markShipped(InventoryLot lot) {
        lot.transitionTo(LotState.SHIPPED);
    }
}
