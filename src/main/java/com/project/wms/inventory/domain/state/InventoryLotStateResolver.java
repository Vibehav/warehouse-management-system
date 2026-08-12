package com.project.wms.inventory.domain.state;

import com.project.wms.inventory.domain.InventoryLot;
import com.project.wms.inventory.enums.LotState;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InventoryLotStateResolver {

    private final Map<LotState, InventoryLotState> states = Map.of(
          LotState.RECEIVED,  new ReceivedState(),
            LotState.AVAILABLE, new AvailableState(),
            LotState.SHIPPED,   new ShippedState(),
            LotState.DAMAGED,   new DamagedState(),
            LotState.EXPIRED,   new ExpiredState());

    public InventoryLotState resolve(InventoryLot lot) {
        return states.get(lot.getState());
    }
}