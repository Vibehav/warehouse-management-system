package com.project.wms.fulfillment.repository;

import com.project.wms.fulfillment.domain.FulfillmentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FulfillmentOrderRepository extends JpaRepository<FulfillmentOrder, Long> {
    List<FulfillmentOrder> findByRequestedBy_Id(Long userId);
}
