package com.fawry.orderservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionModel {

  private String CardNumber;

  private String method;

  private double amount;
}
