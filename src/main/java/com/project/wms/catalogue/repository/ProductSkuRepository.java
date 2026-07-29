package com.project.wms.catalogue.repository;

import com.project.wms.catalogue.folder.ProductSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {
    Optional<ProductSku> findBySkuCode(String skuCode);
    List<ProductSku> findBySupplierId(Long supplierId);
}

