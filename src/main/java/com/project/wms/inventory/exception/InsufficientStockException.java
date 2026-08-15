package com.project.wms.inventory.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String skuCode, int requested, int fulfilled) {
        super("Insufficient stock for SKU " + skuCode + " — requested " + requested
                + ", could only reserve " + fulfilled);
    }
}