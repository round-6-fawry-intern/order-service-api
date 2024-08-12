package com.fawry.orderservice.service;

import com.fawry.orderservice.dto.OrderDto;
import com.fawry.orderservice.dto.OrderRequestModel;

import java.util.Date;
import java.util.List;

public interface OrderService {
  OrderDto createOrder(OrderRequestModel orderDto);

  List<OrderDto> findOrdersByGuestEmail(String guestEmail);

  List<OrderDto> findOrdersByCreatedAtBetween(Date from, Date to);
}
