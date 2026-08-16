package com.project.wms.fulfillment.domain.state;

import com.project.wms.fulfillment.domain.FulfillmentOrder;

public interface FulfillmentOrderState {
    default void allocate(FulfillmentOrder order)  { throw illegal(order, "allocate"); }
    default void cancel(FulfillmentOrder order)    { throw illegal(order, "cancel"); }

    private IllegalStateException illegal(FulfillmentOrder order, String action) {
        return new IllegalStateException(
                "Cannot " + action + " order " + order.getId() + " from status " + order.getStatus());
    }
}
