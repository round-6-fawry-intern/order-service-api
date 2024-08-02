package com.fawry.orderservice.controller;

import com.fawry.orderservice.dto.OrderDto;
import com.fawry.orderservice.dto.OrderRequestModel;
import com.fawry.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
public class OrderController {

    private OrderService orderService;

    @PostMapping
    public OrderDto createOrder(@RequestBody OrderRequestModel order) {

        return orderService.createOrder(order);

    }

}
