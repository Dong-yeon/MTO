package com.order.make.order;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.order.make.order.dto.ConfirmOrderRequest;
import com.order.make.order.dto.ConfirmOrderResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/confirm")
    public ResponseEntity<ConfirmOrderResponse> confirmOrder(@RequestBody @Validated ConfirmOrderRequest request) {
        ConfirmOrderResponse response = orderService.confirmOrder(request);
        return ResponseEntity.ok(response);
    }
}
