package com.project.wms.catalogue.repository;

import com.project.wms.catalogue.domain.ProductSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {
    boolean existsBySkuCodeAndDeletedFalse(String skuCode);

    Optional<ProductSku> findByIdAndDeletedFalse(Long id);

    List<ProductSku> findAllByDeletedFalse();

    Optional<ProductSku> findByIdAndDeletedTrue(Long id);
}

