package com.order.make.order;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.order.make.inventory.InventoryService;
import com.order.make.inventory.dto.StockAllocationResult;
import com.order.make.order.dto.ConfirmOrderRequest;
import com.order.make.order.dto.ConfirmOrderResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;

    @Transactional
    public ConfirmOrderResponse confirmOrder(ConfirmOrderRequest request) {
        // 1. 재고 할당 (원자적으로 처리됨)
        StockAllocationResult allocationResult = inventoryService.allocateStock(
                request.getProductId(),
                request.getQuantity()
        );

        // 2. 할당 결과에 따라 주문 상태 결정
        OrderStatus status = allocationResult.isFullyAllocated()
                ? OrderStatus.READY_TO_SHIP
                : OrderStatus.SCHEDULED_FOR_PRODUCTION;

        String allocationType = determineAllocationType(
                allocationResult.getAllocatedQuantity(),
                allocationResult.getRequestedQuantity()
        );

        LocalDateTime confirmedDeliveryAt = allocationResult.isFullyAllocated()
                ? LocalDateTime.now().plusDays(1)  // 즉시 출하 가능
                : null;  // 생산 후 확정

        // 3. 주문 생성
        Order order = Order.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .status(status)
                .deficitQuantity(allocationResult.getDeficitQuantity() > 0
                        ? allocationResult.getDeficitQuantity()
                        : null)
                .allocationType(allocationType)
                .requestedDeliveryAt(request.getRequestedDeliveryAt())
                .confirmedDeliveryAt(confirmedDeliveryAt)
                .build();

        Order savedOrder = orderRepository.save(order);

        // 4. 응답 생성
        return ConfirmOrderResponse.builder()
                .orderId(savedOrder.getId())
                .status(status)
                .allocatedQuantity(allocationResult.getAllocatedQuantity())
                .deficitQuantity(allocationResult.getDeficitQuantity() > 0
                        ? allocationResult.getDeficitQuantity()
                        : null)
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
