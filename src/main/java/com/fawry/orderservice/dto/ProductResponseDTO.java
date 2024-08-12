package com.fawry.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

  private int id;
  private String category;
  private String name;
  private String description;
  private double price;
  private String imageUrl;
  private int quantity;
}
