package com.fawry.orderservice.service;

import com.fawry.orderservice.dto.ItemRequestModel;
import com.fawry.orderservice.dto.ProductResponseModel;
import com.fawry.orderservice.dto.TransactionModel;

import java.util.List;

public interface WebClientService {

  void validateCoupon(String couponCode, String customerEmail);

  void validateProductOutOfStock(List<ItemRequestModel> itemRequestModels);

  List<ProductResponseModel> getProducts(List<Integer> productIds);

  double calculateDiscount(String couponCode, String customerEmail, double invoiceAmount);

  void withdrawInvoiceAmountFromGuestBankAccount(TransactionModel withdrawRequestModel);

  void depositInvoiceAmountIntoMerchantBankAccount(TransactionModel depositRequestModel);

  void consumeStock(List<ItemRequestModel> itemRequests);

}
