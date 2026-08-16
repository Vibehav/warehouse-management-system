package com.project.wms.fulfillment.domain;

import com.project.wms.catalogue.domain.ProductSku;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fulfillment_order_line")
@Getter @Setter
@NoArgsConstructor
public class FulfillmentOrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fulfillment_order_id", nullable = false)
    private FulfillmentOrder fulfillmentOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_sku_id", nullable = false)
    private ProductSku productSku;

    @Column(nullable = false)
    private int quantity;
}
