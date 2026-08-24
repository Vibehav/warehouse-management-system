package com.project.wms.fulfillment.facade;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.wms.auth.entity.User;
import com.project.wms.auth.repository.UserRepository;
import com.project.wms.catalogue.domain.ProductSku;
import com.project.wms.catalogue.exception.ProductSkuNotFoundException;
import com.project.wms.catalogue.repository.ProductSkuRepository;
import com.project.wms.fulfillment.domain.FulfillmentOrder;
import com.project.wms.fulfillment.domain.FulfillmentOrderLine;
import com.project.wms.fulfillment.domain.FulfillmentOrderLineReservation;
import com.project.wms.fulfillment.domain.state.FulfillmentOrderStateResolver;
import com.project.wms.fulfillment.exception.FulfillmentOrderNotFoundException;
import com.project.wms.fulfillment.repository.FulfillmentOrderLineReservationRepository;
import com.project.wms.fulfillment.repository.FulfillmentOrderRepository;
import com.project.wms.inventory.repository.InventoryBalanceRepository;
import com.project.wms.inventory.service.FulfillmentReservationService;
import com.project.wms.inventory.service.InventoryBalanceService;
import com.project.wms.warehouse.domain.Warehouse;
import com.project.wms.warehouse.exception.WarehouseNotFoundException;
import com.project.wms.warehouse.repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;

/**
 * Facade pattern — orchestrates order creation, allocation, and cancellation
 * behind three simple calls. Callers never talk to FulfillmentReservationService,
 * InventoryBalanceService, or the state resolver directly.
 */
@Component
@RequiredArgsConstructor
public class FulfillmentOrderFacade {

    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductSkuRepository skuRepository;
    private final FulfillmentOrderRepository orderRepository;
    private final FulfillmentOrderLineReservationRepository reservationRepository;
    private final InventoryBalanceRepository balanceRepository;
    private final FulfillmentReservationService reservationService;
    private final InventoryBalanceService balanceService;
    private final FulfillmentOrderStateResolver stateResolver;

    /** Creates the order + lines only. No stock is touched yet. */
    @Transactional
    public FulfillmentOrder createOrder(Long userId, Long warehouseId, List<OrderLineRequest> requestedLines) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found: " + warehouseId));

        FulfillmentOrder.FulfillmentOrderBuilder builder = FulfillmentOrder.builder()
                .requestedBy(user)
                .warehouse(warehouse);

        for (OrderLineRequest lineRequest : requestedLines) {
            ProductSku sku = skuRepository.findByIdAndDeletedFalse(lineRequest.skuId())
                    .orElseThrow(() -> new ProductSkuNotFoundException("SKU not found: " + lineRequest.skuId()));
            builder.addLine(sku, lineRequest.quantity());
        }

        return orderRepository.save(builder.build());
    }

    /**
     * Reserves stock for EVERY line via FulfillmentReservationService, records
     * exactly which balances each line drew from (so cancel() can release
     * them later), then transitions CREATED -> ALLOCATED. All-or-nothing:
     * if any line can't be fully reserved, @Transactional rolls back every
     * reservation made so far in this call.
     */
    @Transactional
    public FulfillmentOrder allocateOrder(Long orderId) {
        FulfillmentOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new FulfillmentOrderNotFoundException("Order not found: " + orderId));

        for (FulfillmentOrderLine line : order.getLines()) {
            FulfillmentReservationService.FulfillmentReservationResult result = reservationService.reserve(
                    line.getProductSku().getId(), order.getWarehouse().getId(), line.getQuantity());

            for (FulfillmentReservationService.ReservationLine reservationLine : result.lines()) {
                FulfillmentOrderLineReservation record = new FulfillmentOrderLineReservation();
                record.setOrderLine(line);
                record.setInventoryBalance(balanceRepository.getReferenceById(reservationLine.balanceId()));
                record.setQuantity(reservationLine.quantity());
                reservationRepository.save(record);
            }
        }

        stateResolver.resolve(order).allocate(order);
        return orderRepository.save(order);
    }

    @Transactional
    public FulfillmentOrder cancelOrder(Long orderId) {
        FulfillmentOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new FulfillmentOrderNotFoundException("Order not found: " + orderId));

        List<FulfillmentOrderLineReservation> reservations =
                reservationRepository.findByOrderLine_FulfillmentOrder_Id(orderId);

        for (FulfillmentOrderLineReservation reservation : reservations) {
            balanceService.release(reservation.getInventoryBalance().getId(), reservation.getQuantity());
        }

        stateResolver.resolve(order).cancel(order);
        return orderRepository.save(order);
    }

    public record OrderLineRequest(Long skuId, int quantity) {}
}
