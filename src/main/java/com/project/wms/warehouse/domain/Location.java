package com.project.wms.warehouse.domain;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private LocationType type;

    private boolean active = true;

    public enum LocationType { BIN }
}
