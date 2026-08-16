package com.project.wms.warehouse.exception;

public class LocationCodeAlreadyExistsException extends RuntimeException {
    public LocationCodeAlreadyExistsException(String message) {
        super(message);
    }
}
