package com.order.make.order;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.order.make.inventory.InventoryService;
import com.order.make.order.dto.ConfirmOrderRequest;
import com.order.make.order.dto.ConfirmOrderResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public ConfirmOrderResponse confirmOrder(ConfirmOrderRequest request) {
        int availableQuantity = inventoryService.getAvailableQuantity(request.getProductId());
        int requestedQuantity = request.getQuantity();
        int allocatedQuantity = Math.min(availableQuantity, requestedQuantity);
        int deficitQuantity = requestedQuantity - allocatedQuantity;

        boolean fullyAllocated = deficitQuantity == 0;
        OrderStatus status = fullyAllocated ? OrderStatus.READY_TO_SHIP : OrderStatus.SCHEDULED_FOR_PRODUCTION;
        String allocationType = determineAllocationType(allocatedQuantity, requestedQuantity);
        LocalDateTime confirmedDeliveryAt = fullyAllocated ? LocalDateTime.now() : null;

        Order order = Order.builder()
                .productId(request.getProductId())
                .quantity(requestedQuantity)
                .status(status)
                .deficitQuantity(deficitQuantity > 0 ? deficitQuantity : null)
                .allocationType(allocationType)
                .requestedDeliveryAt(request.getRequestedDeliveryAt())
                .confirmedDeliveryAt(confirmedDeliveryAt)
                .build();

        Order savedOrder = orderRepository.save(order);

        return ConfirmOrderResponse.builder()
                .orderId(savedOrder.getId())
                .status(status)
                .allocatedQuantity(allocatedQuantity)
                .deficitQuantity(deficitQuantity > 0 ? deficitQuantity : null)
                .allocationType(allocationType)
                .confirmedDeliveryAt(confirmedDeliveryAt)
                .build();
    }

    private String determineAllocationType(int allocatedQuantity, int requestedQuantity) {
        if (allocatedQuantity == 0) {
            return "BACKORDER";
        }
        if (allocatedQuantity == requestedQuantity) {
            return "INVENTORY";
        }
        return "PARTIAL";
    }
}
