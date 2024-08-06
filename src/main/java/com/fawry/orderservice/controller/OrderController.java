package com.fawry.orderservice.controller;

import com.fawry.orderservice.dto.OrderDto;
import com.fawry.orderservice.dto.OrderRequestModel;
import com.fawry.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderDto createOrder(@RequestBody OrderRequestModel order) {
    System.out.println(order.toString());
        return orderService.createOrder(order);

    }

}
