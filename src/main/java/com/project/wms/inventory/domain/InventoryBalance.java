package com.project.wms.inventory.domain;

import com.project.wms.warehouse.domain.Location;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory_balance")
@Getter
@Setter
@NoArgsConstructor
public class InventoryBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_lot_id", nullable = false)
    private InventoryLot inventoryLot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;

    // InventoryBalance.java — add this field
    @Column(nullable = false)
    private boolean confirmed = false;

    @Version
    private int version; // optimistic locking — THIS is the contended row now

    public int availableQuantity() {
        return quantity - reservedQuantity;
    }

    public static InventoryBalance of(InventoryLot lot, Location location, int quantity) {
        if (lot == null) {
            throw new IllegalArgumentException("inventoryLot is required");
        }
        if (location == null) {
            throw new IllegalArgumentException("location is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        InventoryBalance balance = new InventoryBalance();
        balance.setInventoryLot(lot);
        balance.setLocation(location);
        balance.setQuantity(quantity);
        return balance;
    }
}