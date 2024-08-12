package com.fawry.orderservice.entity;

import com.fawry.orderservice.entity.OrderItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "customer_email")
  private String customerEmail;

  @Column(name = "coupon_code")
  private String couponCode;

  @Column(name = "amount")
  private double amount;

  @Column(name = "created_at")
  private Timestamp createdAt;

  @Column(name = "updated_at")
  private Timestamp updatedAt;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
  private List<OrderItem> orderItems;
}
