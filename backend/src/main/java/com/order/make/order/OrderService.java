package com.order.make.order;

import com.order.make.order.dto.ConfirmOrderRequest;
import com.order.make.order.dto.ConfirmOrderResponse;

public interface OrderService {

    ConfirmOrderResponse confirmOrder(ConfirmOrderRequest request);
}
