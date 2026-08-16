package com.project.wms.common.exception;

import com.project.wms.auth.exception.EmailAlreadyRegisteredException;
import com.project.wms.auth.exception.InvalidCredentialsException;
import com.project.wms.auth.exception.InvalidRefreshTokenException;
import com.project.wms.auth.exception.RegistrationClosedException;
import com.project.wms.auth.exception.RolesNotFoundException;
import com.project.wms.auth.exception.UserNotFoundException;
import com.project.wms.catalogue.exception.ProductSkuCodeAlreadyExistsException;
import com.project.wms.catalogue.exception.ProductSkuNotFoundException;
import com.project.wms.fulfillment.exception.FulfillmentOrderNotFoundException;
import com.project.wms.inventory.exception.InsufficientStockException;
import com.project.wms.inventory.exception.InventoryBalanceNotFound;
import com.project.wms.supplier.exception.SupplierCodeAlreadyExistsException;
import com.project.wms.supplier.exception.SupplierNotFoundException;
import com.project.wms.warehouse.exception.InsufficientCapacityException;
import com.project.wms.warehouse.exception.LocationCodeAlreadyExistsException;
import com.project.wms.warehouse.exception.LocationNotFoundException;
import com.project.wms.warehouse.exception.PlacementRuleNotFoundException;
import com.project.wms.warehouse.exception.WarehouseCodeAlreadyExistsException;
import com.project.wms.warehouse.exception.WarehouseNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- 404 — the id/reference in the request doesn't exist ----
    @ExceptionHandler({
            ProductSkuNotFoundException.class,
            SupplierNotFoundException.class,
            WarehouseNotFoundException.class,
            InventoryBalanceNotFound.class,
            LocationNotFoundException.class,
            PlacementRuleNotFoundException.class,
            UserNotFoundException.class,
            RolesNotFoundException.class,
            FulfillmentOrderNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.of(404, "Not Found", ex.getMessage()));
    }

    // ---- 409 — valid request, but current system/business state won't allow it ----
    @ExceptionHandler({
            InsufficientCapacityException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.of(409, "Conflict", ex.getMessage()));
    }

    // ---- 409 — the request is well-formed, but the unique code it wants is already taken ----
    @ExceptionHandler({
            EmailAlreadyRegisteredException.class,
            WarehouseCodeAlreadyExistsException.class,
            LocationCodeAlreadyExistsException.class,
            SupplierCodeAlreadyExistsException.class,
            ProductSkuCodeAlreadyExistsException.class
    })
    public ResponseEntity<ErrorResponse> handleAlreadyExists(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.of(409, "Conflict", ex.getMessage()));
    }

    // ---- 400 — malformed or invalid input from the client ----
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of(400, "Bad Request", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of(500, "Internal Server Error", "An unexpected error occurred"));
    }

    // ----------- auth --------------------------

    // 401 — caller's credentials or token are wrong/expired, not malformed input
    @ExceptionHandler({InvalidRefreshTokenException.class, InvalidCredentialsException.class
    })
    public ResponseEntity<ErrorResponse> handleUnauthorized(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(401, "Unauthorized", ex.getMessage()));
    }

    // 403 — self-registration only works for the very first user; everyone after is created by an admin
    @ExceptionHandler(RegistrationClosedException.class)
    public ResponseEntity<ErrorResponse> handleRegistrationClosed(RegistrationClosedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.of(403, "Forbidden", ex.getMessage()));
    }

    // ----------- inventory --------------------------

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.of(409, "Conflict", ex.getMessage()));
    }

    @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockConflict(
            org.springframework.orm.ObjectOptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.of(409, "Conflict", "This record was updated concurrently — please retry"));
    }

}