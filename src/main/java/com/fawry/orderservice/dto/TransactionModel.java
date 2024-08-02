package com.fawry.orderservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionModel {

  private String cardNumber;

  private String cvv;

  private Double amount;
}
