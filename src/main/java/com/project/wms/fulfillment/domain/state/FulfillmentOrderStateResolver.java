package com.project.wms.fulfillment.domain.state;

import com.project.wms.fulfillment.domain.FulfillmentOrder;
import com.project.wms.fulfillment.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FulfillmentOrderStateResolver {

    private final Map<OrderStatus, FulfillmentOrderState> states = Map.of(
            OrderStatus.CREATED, new CreatedState(),
            OrderStatus.ALLOCATED, new AllocatedState(),
            OrderStatus.DELIVERED, new DeliveredState(),
            OrderStatus.CANCELLED, new CancelledState()
    );

    public FulfillmentOrderState resolve(FulfillmentOrder order) {
        return states.get(order.getStatus());
    }
}
