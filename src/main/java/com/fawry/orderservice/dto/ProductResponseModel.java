package com.fawry.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseModel {

  private int id;
  private String code;
  private String name;
  private String description;
  private double price;
  private String image;
  private int quantity;
}
