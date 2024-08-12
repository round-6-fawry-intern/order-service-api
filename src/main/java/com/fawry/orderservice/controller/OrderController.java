package com.fawry.orderservice.controller;

import com.fawry.orderservice.dto.OrderDto;
import com.fawry.orderservice.dto.OrderRequestModel;
import com.fawry.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

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

  @GetMapping("/{guestEmail:.+}")
  public List<OrderDto> ordersMadeByCustomer(@PathVariable String guestEmail) {
    return orderService.findOrdersByGuestEmail(guestEmail);
  }

  @GetMapping
  public List<OrderDto> findOrdersByCreatedAtBetween(@RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
                                                     @RequestParam(value = "to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
    if (to == null) {
      to = new Date();
    }
    if (from == null) {
      from = new Date(to.getTime() - 10 * 31536000000L);
    }
    return orderService.findOrdersByCreatedAtBetween(from, to);
  }
}
