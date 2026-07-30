package com.project.wms.inventory.domain;

import com.project.wms.catalogue.folder.ProductSku;
import com.project.wms.inventory.enums.LotState;
import com.project.wms.supplier.domain.Supplier;
import com.project.wms.warehouse.domain.Location;
import com.project.wms.warehouse.domain.Warehouse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

// InventoryLot.java — note: NO @Setter, no public constructor.
// This entity can only be constructed via the Builder — enforced on purpose.
@Entity
@Table(name = "inventory_lot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // required by JPA, not for app use
public class InventoryLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_sku_id", nullable = false)
    private ProductSku productSku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "batch_no")
    private String batchNo;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private LotState state;

    @Version
    private int version; // optimistic locking column

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // Only Builder can call it
    InventoryLot(ProductSku productSku, Supplier supplier, Warehouse warehouse, Location location,
                 String batchNo, LocalDate expiryDate, int quantity, LotState state) {
        this.productSku = productSku;
        this.supplier = supplier;
        this.warehouse = warehouse;
        this.location = location;
        this.batchNo = batchNo;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.state = state;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public static InventoryLotBuilder builder() {
        return new InventoryLotBuilder();
    }

    public static class InventoryLotBuilder {

        private ProductSku productSku;
        private Supplier supplier;
        private Warehouse warehouse;
        private Location location;
        private String batchNo;
        private LocalDate expiryDate;
        private int quantity;
        private LotState state = LotState.RECEIVED; // sensible default

        public InventoryLotBuilder productSku(ProductSku productSku) {
            this.productSku = productSku;
            return this;
        }

        public InventoryLotBuilder supplier(Supplier supplier) {
            this.supplier = supplier;
            return this;
        }

        public InventoryLotBuilder warehouse(Warehouse warehouse) {
            this.warehouse = warehouse;
            return this;
        }

        public InventoryLotBuilder location(Location location) {
            this.location = location;
            return this;
        }

        public InventoryLotBuilder batchNo(String batchNo) {
            this.batchNo = batchNo;
            return this;
        }

        public InventoryLotBuilder expiryDate(LocalDate expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public InventoryLotBuilder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public InventoryLot build() {
            Objects.requireNonNull(productSku, "productSku is required");
            Objects.requireNonNull(supplier, "supplier is required");
            Objects.requireNonNull(warehouse, "warehouse is required");
            Objects.requireNonNull(location, "location is required");
            if (quantity <= 0) {
                throw new IllegalArgumentException("quantity must be positive");
            }
            return new InventoryLot(productSku, supplier, warehouse, location, batchNo, expiryDate, quantity, state);
        }
    }
    public void transitionTo(LotState newState) {
        this.state = newState;
        this.updatedAt = Instant.now();
    }

}