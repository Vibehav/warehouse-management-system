package com.project.wms.inventory.domain.state;

import com.project.wms.inventory.domain.InventoryLot;
import com.project.wms.inventory.enums.LotState;

public class ReceivedState implements InventoryLotState {

    @Override
    public void markAvailable(InventoryLot lot) {
        lot.transitionTo(LotState.AVAILABLE);
    }

    @Override
    public void markDamaged(InventoryLot lot) {
        lot.transitionTo(LotState.DAMAGED);
    }
}
