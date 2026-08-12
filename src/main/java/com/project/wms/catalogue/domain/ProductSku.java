package com.project.wms.catalogue.domain;

import com.project.wms.common.enums.ProductCategory;
import com.project.wms.common.enums.StorageZoneType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "sku_code", nullable = false, unique = true)
    private String skuCode;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "prouct_category",nullable = false)
    private ProductCategory category;  // FMCG,PHARMA,APPAREL,CHEMICAL

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_zone_type", nullable = false)
    private StorageZoneType storageZoneType;

    private Double weight;   // kg
    private Double length;   // cm
    private Double width;
    private Double height;

    @Column(nullable = false)
    private boolean deleted = false;

}