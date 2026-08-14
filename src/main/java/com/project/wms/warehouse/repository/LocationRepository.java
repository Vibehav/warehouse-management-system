package com.project.wms.warehouse.repository;

import com.project.wms.common.enums.StorageZoneType;
import com.project.wms.warehouse.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByWarehouseIdAndStorageZoneTypeAndActiveTrueAndBlockedFalse(Long warehouseId, StorageZoneType storageZoneType);
    List<Location> findByWarehouseIdAndActiveTrue(Long warehouseId);
    Optional<Location> findByIdAndActiveTrue(Long id);
    boolean existsByWarehouseIdAndCode(Long warehouseId, String code);

    @Query(
            "SELECT l FROM Location l WHERE l.warehouse.id=:warehouseId AND l.id=:id AND l.active=TRUE"
    )
    Optional<Location> findActiveLocationByIdAndWarehouseId(
            @Param("id") Long id,
            @Param("warehouseId") Long warehouseId
    );

    @Query(
            "SELECT l FROM Location l WHERE l.warehouse.id=:warehouseId AND l.id=:id AND l.active=FALSE"
    )
    Optional<Location> findInActiveLocationByIdAndWarehouseId(
            @Param("id") Long id,
            @Param("warehouseId") Long warehouseId
    );

    @Query(
            "SELECT l FROM Location l WHERE l.warehouse.id=:warehouseId AND l.id=:id AND l.blocked=FALSE"
    )
    Optional<Location> findUnblockedLocationByIdAndWarehouseId(
            @Param("id") Long id,
            @Param("warehouseId") Long warehouseId
    );

    @Query(
            "SELECT l FROM Location l WHERE l.warehouse.id=:warehouseId AND l.id=:id AND l.blocked=true"
    )
    Optional<Location> findBlockedLocationByIdAndWarehouseId(
            @Param("id") Long id,
            @Param("warehouseId") Long warehouseId
    );



   }


