package com.fawry.orderservice.service;

import com.fawry.orderservice.dto.ItemRequestModel;
import com.fawry.orderservice.dto.OrderDto;
import com.fawry.orderservice.dto.OrderRequestModel;
import com.fawry.orderservice.dto.ProductResponseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private WebClientService webClientService;

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

      webClientService.validateCoupon(
          orderRequestModel.getCouponCode(), orderRequestModel.getCustomerEmail());

      totalAmountAfterDiscount =
          webClientService.calculateDiscount(
              orderRequestModel.getCouponCode(), orderRequestModel.getCustomerEmail(), totalAmount);
    }
    // apply payment with bank api

    webClientService.depositInvoiceAmountIntoMerchantBankAccount(
        orderRequestModel.getTransactionModel());
    webClientService.withdrawInvoiceAmountFromGuestBankAccount(
        orderRequestModel.getTransactionModel());

    // consume stock
    webClientService.consumeStock(orderRequestModel.getItems());


    // create order in  my db

    // send notifications

    return null;
  }
}
