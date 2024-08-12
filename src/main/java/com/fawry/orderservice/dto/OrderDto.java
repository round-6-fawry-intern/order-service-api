package com.fawry.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {

    private int id;
    private String customerEmail;
    private String couponCode;
    private double amount;
    private Timestamp createdAt;
    private List<ItemResponseModel> orderItems;
}
