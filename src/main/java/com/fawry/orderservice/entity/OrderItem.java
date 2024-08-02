package com.fawry.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Table(name = "order_items")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "order_id")
  private int orderId;

  @Column(name = "product_id")
  private int productId;

  @Column(name = "price")
  private double price;

  @Column(name = "quantity")
  private int quantity;
}
