package com.fawry.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "order_items")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "product_id")
  private int productId;

  @Column(name = "price")
  private double price;

  @Column(name = "quantity")
  private int quantity;

  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;

  @Override
  public String toString() {
    return "OrderItem{" +
            "id=" + id +
            ", productId=" + productId +
            ", price=" + price +
            ", quantity=" + quantity +
            '}';
  }
}
