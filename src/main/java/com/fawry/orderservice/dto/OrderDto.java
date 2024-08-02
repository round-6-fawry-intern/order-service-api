package com.fawry.orderservice.dto;

import java.sql.Timestamp;
import java.util.List;

public class OrderDto {

    private int id;
    private String customerEmail;
    private String couponCode;
    private double amount;
    private Timestamp createdAt;
    private List<ItemRequestModel> orderItems;
}
