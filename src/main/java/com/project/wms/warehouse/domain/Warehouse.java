package com.project.wms.warehouse.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "warehouse")
@Getter
@Setter
@NoArgsConstructor
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "placement_strategy_type", nullable = false)
    private String placementStrategyType; // resolved to a StockPlacementStrategy bean on Day 2

    private boolean active = true;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Location> locations = new ArrayList<>();
}
