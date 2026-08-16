package com.project.wms.fulfillment.domain.state;

import com.project.wms.fulfillment.domain.FulfillmentOrder;
import com.project.wms.fulfillment.enums.OrderStatus;

public class AllocatedState implements FulfillmentOrderState {
    @Override public void cancel(FulfillmentOrder order) { order.transitionTo(OrderStatus.CANCELLED); }
}
