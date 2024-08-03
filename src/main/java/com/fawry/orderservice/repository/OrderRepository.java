package com.fawry.orderservice.repository;

import com.fawry.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository< Order,Integer> {}
