package com.project.wms.fulfillment.domain;

import com.project.wms.inventory.domain.InventoryBalance;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Records exactly which InventoryBalance rows an order line's allocation
 * drew from, and how much from each — the persisted equivalent of
 * FulfillmentReservationService's in-memory ReservationLine. Without this,
 * cancelling an ALLOCATED order has no way to know what to release.
 */
@Entity
@Table(name = "fulfillment_order_line_reservation")
@Getter @Setter
@NoArgsConstructor
public class FulfillmentOrderLineReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fulfillment_order_line_id", nullable = false)
    private FulfillmentOrderLine orderLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_balance_id", nullable = false)
    private InventoryBalance inventoryBalance;

    @Column(nullable = false)
    private int quantity;
}
