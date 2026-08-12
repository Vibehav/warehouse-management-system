package com.project.wms.inventory.domain.state;

import com.project.wms.inventory.domain.InventoryLot;
import com.project.wms.inventory.enums.LotState;

public class DamagedState implements InventoryLotState{
    @Override
    public void markDamaged(InventoryLot lot) {
        lot.transitionTo(LotState.DAMAGED);
    }
}
