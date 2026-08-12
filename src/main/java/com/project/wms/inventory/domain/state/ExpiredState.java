package com.project.wms.inventory.domain.state;

import com.project.wms.inventory.domain.InventoryLot;
import com.project.wms.inventory.enums.LotState;

public class ExpiredState implements InventoryLotState{
    @Override
    public void expire(InventoryLot lot) {
        lot.transitionTo(LotState.EXPIRED);
    }
}
