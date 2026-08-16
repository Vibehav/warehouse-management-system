package com.project.wms.warehouse.exception;

public class WarehouseCodeAlreadyExistsException extends RuntimeException {
    public WarehouseCodeAlreadyExistsException(String message) {
        super(message);
    }
}
