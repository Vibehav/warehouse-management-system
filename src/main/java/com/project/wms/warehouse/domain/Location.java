package com.project.wms.warehouse.domain;

import com.project.wms.common.enums.StorageZoneType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Location.java
@Entity
@Table(name = "location")
@Getter
@Setter
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_zone_type", nullable = false)
    private StorageZoneType storageZoneType;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    private boolean active = true;
    private boolean blocked = false;

}
