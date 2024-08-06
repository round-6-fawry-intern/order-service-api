package com.fawry.orderservice.service;

import com.fawry.orderservice.dto.*;
import com.fawry.orderservice.entity.Order;
import com.fawry.orderservice.entity.OrderItem;
import com.fawry.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final WebClientService webClientService;
  private final OrderRepository orderRepository;

  @Value("${system_bank_number}")
  private String systemBankNumber;

  @Value("${system_bank_cvv}")
  private String bankAccountCvv;

  @Override
  public OrderDto createOrder(OrderRequestModel orderRequestModel) {
    // check product quantity
//    webClientService.validateProductOutOfStock(orderRequestModel.getItems());

    // get all products by ids

    List<ProductResponseModel> products =
        webClientService.getProducts(
            orderRequestModel.getItems().stream().map(ItemRequestModel::getProductId).toList());

    // calc total amount for all products
    double totalAmount = orderRequestModel.getItems().stream()
            .mapToDouble(item -> {
              ProductResponseModel product = products.stream()
                      .filter(p -> p.getId() == item.getProductId())
                      .findFirst()
                      .orElseThrow(() -> new RuntimeException("Product not found"));
              return product.getPrice() * item.getQuantity();
            })
            .sum();

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
    //
    System.out.println(orderRequestModel.getCardNumber());
    TransactionModel customerTransaction =
        TransactionModel.builder()
            .amount(totalAmountAfterDiscount)
            .CardNumber(orderRequestModel.getCardNumber())
            .method("Draw")
            .build();

    System.out.println(customerTransaction.getCardNumber());

    webClientService.withdrawInvoiceAmountFromGuestBankAccount(customerTransaction);

    TransactionModel merchantTransaction =
        TransactionModel.builder()
            .method("Add")
            .amount(totalAmountAfterDiscount)
            .CardNumber(systemBankNumber)
            .build();

    webClientService.depositInvoiceAmountIntoMerchantBankAccount(merchantTransaction);

    // create order in  my db
    //

    Order order =
        Order.builder()
            .customerEmail(orderRequestModel.getCustomerEmail())
            .couponCode(orderRequestModel.getCouponCode())
            .amount(totalAmountAfterDiscount)
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .updatedAt(new Timestamp(System.currentTimeMillis()))
            .orderItems(
                orderRequestModel.getItems().stream()
                    .map(
                        item ->
                            OrderItem.builder()
                                .productId(item.getProductId())
                                .price(
                                    products.stream()
                                        .filter(p -> p.getId() == item.getProductId())
                                        .findFirst()
                                        .map(ProductResponseModel::getPrice)
                                        .orElse(0.0))
                                .quantity(item.getQuantity())
                                .build())
                    .toList())
            .build();

    Order createdOrder = orderRepository.save(order);

    System.out.println(createdOrder);
    // consume stock

//    webClientService.consumeStock(orderRequestModel.getItems());

    //     Consume Copoun -----

    CouponRequsetModel couponRequest =
        CouponRequsetModel.builder()
            .couponCode(orderRequestModel.getCouponCode())
            .orderId(order.getId())
            .userEmail(orderRequestModel.getCustomerEmail())
            .build();

    webClientService.consumeCoupon(couponRequest);

    // send notifications

    String notificationMessage = "Order completed with total price " + totalAmountAfterDiscount;

    System.out.println(notificationMessage);

    NotificationDto notificationDto =
        NotificationDto.builder()
            .message(notificationMessage)
            .customerEmail(orderRequestModel.getCustomerEmail())
            .build();

    webClientService.sendOrderDetailsToNotificationsAPI(notificationDto);

    return null;
  }
}
