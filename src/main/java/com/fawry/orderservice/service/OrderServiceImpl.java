package com.fawry.orderservice.service;

import com.fawry.orderservice.dto.*;
import com.fawry.orderservice.entity.Order;
import com.fawry.orderservice.entity.OrderItem;
import com.fawry.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final WebClientService webClientService;
  private final OrderRepository orderRepository;

  @Override
  public OrderDto createOrder(OrderRequestModel orderRequestModel) {
    // check product quantity
        webClientService.validateProductOutOfStock(orderRequestModel.getItems());

    // get all products by ids

        List<ProductResponseModel> products =
            webClientService.getProducts(

     orderRequestModel.getItems().stream().map(ItemRequestModel::getProductId).toList());


    // calc total amount for all products

    double totalAmount =
        products.stream().map(ProductResponseModel::getPrice).reduce(0.0, Double::sum);

    // apply discount  if coupon exist
    double totalAmountAfterDiscount = totalAmount;
    if (orderRequestModel.getCouponCode() != null) {

      //  check  coupon validation ------200 ----400 //params //get
      webClientService.validateCoupon(
          orderRequestModel.getCouponCode(), orderRequestModel.getCustomerEmail());

      // total amount Post --Body
      totalAmountAfterDiscount =
          webClientService.calculateDiscount(
              orderRequestModel.getCouponCode(), orderRequestModel.getCustomerEmail(), totalAmount);

      System.out.println(totalAmountAfterDiscount);
    }
    // apply payment with bank api

    webClientService.depositInvoiceAmountIntoMerchantBankAccount(
        orderRequestModel.getTransactionModel());
    webClientService.withdrawInvoiceAmountFromGuestBankAccount(
        orderRequestModel.getTransactionModel());

    // consume stock
    webClientService.consumeStock(orderRequestModel.getItems());

    // create order in  my db

    Order order =
        Order.builder()
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .customerEmail(orderRequestModel.getCustomerEmail())
            .amount(totalAmountAfterDiscount)
            .orderItems(
                orderRequestModel.getItems().stream()
                    .map(
                        item ->
                            OrderItem.builder()
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .build())
                    .toList())
            .build();

//    Order createdOrder = orderRepository.save(order);

//     Consume Copoun -----

    CouponRequsetModel couponRequest =
        CouponRequsetModel.builder()
            .couponCode(orderRequestModel.getCouponCode())
            .orderId(order.getId())
            .userEmail(orderRequestModel.getCustomerEmail())
            .build();

    // send notifications

    return null;
  }
}
