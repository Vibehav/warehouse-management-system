package com.project.wms.fulfillment.repository;

import com.project.wms.fulfillment.domain.FulfillmentOrderLineReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FulfillmentOrderLineReservationRepository extends JpaRepository<FulfillmentOrderLineReservation, Long> {
    List<FulfillmentOrderLineReservation> findByOrderLine_FulfillmentOrder_Id(Long orderId);
}
