package com.project.wms.catalogue.exception;

public class ProductSkuCodeAlreadyExistsException extends RuntimeException {
    public ProductSkuCodeAlreadyExistsException(String message) {
        super(message);
    }
}
