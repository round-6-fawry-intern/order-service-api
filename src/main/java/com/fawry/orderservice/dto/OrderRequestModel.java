package com.fawry.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonProperty("CardNumber")
  private String CardNumber;

  private List<ItemRequestModel> items;
}
