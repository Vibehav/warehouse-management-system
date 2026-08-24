package com.project.wms.catalogue.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.wms.catalogue.domain.ProductSku;

@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {
    boolean existsBySkuCode(String skuCode);

    Optional<ProductSku> findByIdAndDeletedFalse(Long id);

    List<ProductSku> findAllByDeletedFalse();

    Optional<ProductSku> findByIdAndDeletedTrue(Long id);
}

