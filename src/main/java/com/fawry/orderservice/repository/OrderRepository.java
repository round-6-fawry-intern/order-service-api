package com.fawry.orderservice.repository;

import com.fawry.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository< Order,Integer> {
    List<Order> findOrdersByCustomerEmail(String customerEmail);

    List<Order> findOrdersByCreatedAtBetween(Date from, Date to);
}
