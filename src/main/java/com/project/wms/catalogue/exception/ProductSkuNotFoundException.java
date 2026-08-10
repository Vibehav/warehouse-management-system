package com.project.wms.catalogue.exception;

public class ProductSkuNotFoundException extends RuntimeException {
    public ProductSkuNotFoundException(String message) {
        super(message);
    }
}
