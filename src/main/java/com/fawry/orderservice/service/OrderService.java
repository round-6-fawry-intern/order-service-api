package com.fawry.orderservice.service;

import com.fawry.orderservice.dto.OrderDto;
import com.fawry.orderservice.dto.OrderRequestModel;

public interface OrderService {
  OrderDto createOrder(OrderRequestModel orderDto);
}
