package com.project.wms.warehouse.repository;

import com.project.wms.warehouse.domain.PlacementRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlacementRuleRepository extends JpaRepository<PlacementRule, Long> {
    List<PlacementRule> findByActiveTrueOrderByPriorityAsc();
    Optional<PlacementRule> findByIdAndActiveTrue(Long id);
}