package com.fawry.orderservice.service;

import com.fawry.orderservice.dto.*;

import java.util.List;

public interface WebClientService {

  void validateCoupon(String couponCode, String customerEmail);

  void validateProductOutOfStock(List<ItemRequestModel> itemRequestModels);

  List<ProductResponseModel> getProducts(List<Integer> productIds);

  double calculateDiscount(String couponCode, String customerEmail, double invoiceAmount);

  void withdrawInvoiceAmountFromGuestBankAccount(TransactionModel withdrawRequestModel);

  void depositInvoiceAmountIntoMerchantBankAccount(TransactionModel depositRequestModel);

  void consumeStock(List<ItemRequestModel> itemRequests);

  void consumeCoupon(CouponRequsetModel couponRequest);

  void sendOrderDetailsToNotificationsAPI(NotificationDto notificationDto);

  StoreResponseDto getStoreById(long storeId);

}
