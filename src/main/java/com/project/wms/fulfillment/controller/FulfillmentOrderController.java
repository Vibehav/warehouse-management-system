package com.project.wms.fulfillment.controller;

import com.project.wms.fulfillment.domain.FulfillmentOrder;
import com.project.wms.fulfillment.dto.*;
import com.project.wms.fulfillment.facade.FulfillmentOrderFacade;
import com.project.wms.fulfillment.repository.FulfillmentOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/fulfillment-orders")
@RequiredArgsConstructor
public class FulfillmentOrderController {

    private final FulfillmentOrderFacade facade;
    private final FulfillmentOrderRepository orderRepository;

    @PostMapping
    @PreAuthorize("hasAuthority('FULFILLMENT_ORDER_CREATE')")
    public ResponseEntity<FulfillmentOrderResponseDto> create(
            Authentication authentication, @RequestBody CreateFulfillmentOrderRequestDto request) {
        Long userId = (Long) authentication.getPrincipal();

        List<FulfillmentOrderFacade.OrderLineRequest> lines = new ArrayList<>();
        for (OrderLineRequestDto lineDto : request.lines()) {
            lines.add(new FulfillmentOrderFacade.OrderLineRequest(lineDto.skuId(), lineDto.quantity()));
        }

        FulfillmentOrder order = facade.createOrder(userId, request.warehouseId(), lines);
        return ResponseEntity.ok(FulfillmentOrderResponseDto.from(order));
    }

    @PostMapping("/{id}/allocate")
    @PreAuthorize("hasAuthority('FULFILLMENT_RESERVE_ALLOCATE')")
    public ResponseEntity<FulfillmentOrderResponseDto> allocate(@PathVariable Long id) {
        FulfillmentOrder order = facade.allocateOrder(id);
        return ResponseEntity.ok(FulfillmentOrderResponseDto.from(order));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('FULFILLMENT_RESERVE_ALLOCATE')")
    public ResponseEntity<FulfillmentOrderResponseDto> cancel(@PathVariable Long id) {
        FulfillmentOrder order = facade.cancelOrder(id);
        return ResponseEntity.ok(FulfillmentOrderResponseDto.from(order));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAuthority('FULFILLMENT_ORDER_VIEW_OWN')")
    public ResponseEntity<List<FulfillmentOrderResponseDto>> myOrders(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<FulfillmentOrderResponseDto> orders = new ArrayList<>();
        for (FulfillmentOrder order : orderRepository.findByRequestedBy_Id(userId)) {
            orders.add(FulfillmentOrderResponseDto.from(order));
        }
        return ResponseEntity.ok(orders);
    }
}
