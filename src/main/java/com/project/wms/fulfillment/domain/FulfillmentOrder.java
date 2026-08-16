package com.project.wms.fulfillment.domain;

import com.project.wms.auth.entity.User;
import com.project.wms.catalogue.domain.ProductSku;
import com.project.wms.fulfillment.enums.OrderStatus;
import com.project.wms.warehouse.domain.Warehouse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "fulfillment_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // use builder() instead
public class FulfillmentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "fulfillmentOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FulfillmentOrderLine> lines = new ArrayList<>();

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    FulfillmentOrder(User requestedBy, Warehouse warehouse, List<FulfillmentOrderLine> lines) {
        this.requestedBy = requestedBy;
        this.warehouse = warehouse;
        this.status = OrderStatus.CREATED;
        this.lines = lines;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        for (FulfillmentOrderLine line : lines) {
            line.setFulfillmentOrder(this);
        }
    }

    public static FulfillmentOrderBuilder builder() {
        return new FulfillmentOrderBuilder();
    }

    public static class FulfillmentOrderBuilder {

        private User requestedBy;
        private Warehouse warehouse;
        private final List<FulfillmentOrderLine> lines = new ArrayList<>();

        public FulfillmentOrderBuilder requestedBy(User requestedBy) { this.requestedBy = requestedBy; return this; }
        public FulfillmentOrderBuilder warehouse(Warehouse warehouse) { this.warehouse = warehouse; return this; }

        public FulfillmentOrderBuilder addLine(ProductSku sku, int quantity) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Line quantity must be positive for SKU " + sku.getSkuCode());
            }
            FulfillmentOrderLine line = new FulfillmentOrderLine();
            line.setProductSku(sku);
            line.setQuantity(quantity);
            lines.add(line);
            return this;
        }

        public FulfillmentOrder build() {
            Objects.requireNonNull(requestedBy, "requestedBy is required");
            Objects.requireNonNull(warehouse, "warehouse is required");
            if (lines.isEmpty()) {
                throw new IllegalArgumentException("An order must have at least one line");
            }
            return new FulfillmentOrder(requestedBy, warehouse, lines);
        }
    }

    // Called only by FulfillmentOrderState implementations
    public void transitionTo(OrderStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }
}
