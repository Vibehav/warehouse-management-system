package com.project.wms.warehouse.domain;

import com.project.wms.common.enums.ConditionType;
import com.project.wms.common.enums.ProductCategory;
import com.project.wms.common.enums.StorageZoneType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "placement_rule")
@Getter @Setter
@NoArgsConstructor
public class PlacementRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false)
    private ConditionType conditionType; // CATEGORY_MATCH, DEFAULT

    @Column(name = "condition_value")
    private String conditionValue; // FMCG,PHARMA

    @Column(name = "strategy_type", nullable = false)
    private String strategyType;   // matches @Component bean name, e.g. "FIRST_AVAILABLE" , "NEAREST_AVAILABLE"

    @Column(nullable = false)
    private int priority;

    private boolean active = true;
}