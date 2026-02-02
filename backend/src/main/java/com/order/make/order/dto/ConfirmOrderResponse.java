package com.order.make.order.dto;

import java.time.LocalDateTime;

import com.order.make.order.OrderStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConfirmOrderResponse {

    private final Long orderId;
    private final OrderStatus status;
    private final Integer allocatedQuantity;
    private final Integer deficitQuantity;
    private final String allocationType;
    private final LocalDateTime confirmedDeliveryAt;
}
