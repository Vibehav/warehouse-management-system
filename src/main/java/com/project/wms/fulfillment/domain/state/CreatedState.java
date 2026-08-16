package com.project.wms.fulfillment.domain.state;

import com.project.wms.fulfillment.domain.FulfillmentOrder;
import com.project.wms.fulfillment.enums.OrderStatus;

public class CreatedState implements FulfillmentOrderState {
    @Override public void allocate(FulfillmentOrder order) { order.transitionTo(OrderStatus.ALLOCATED); }
    @Override public void cancel(FulfillmentOrder order)   { order.transitionTo(OrderStatus.CANCELLED); }
}
