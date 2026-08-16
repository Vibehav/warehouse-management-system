package com.project.wms.fulfillment.exception;

public class FulfillmentOrderNotFoundException extends RuntimeException {
    public FulfillmentOrderNotFoundException(String message) {
        super(message);
    }
}
