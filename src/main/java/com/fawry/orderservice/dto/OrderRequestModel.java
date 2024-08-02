package com.fawry.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestModel {

  private String couponCode;

  private String customerEmail;

  private TransactionModel transactionModel;

  private List<ItemRequestModel> items;
}
