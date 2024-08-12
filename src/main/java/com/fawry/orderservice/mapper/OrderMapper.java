package com.fawry.orderservice.mapper;

import com.fawry.orderservice.dto.OrderDto;
import com.fawry.orderservice.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "orderItems",target = "orderItems")
    OrderDto toOrderDto(Order order);



}
