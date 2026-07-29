package com.project.wms.catalogue.folder;

import com.project.wms.supplier.domain.Supplier;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// ProductSku.java
@Entity
@Table(name = "product_sku")
@Getter
@Setter
@NoArgsConstructor
public class ProductSku {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "sku_code", nullable = false, unique = true)
    private String skuCode;

    @Column(nullable = false)
    private String name;

    private String category;
}