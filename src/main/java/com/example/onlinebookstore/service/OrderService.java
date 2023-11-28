package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.order.OrderRequestDto;
import com.example.onlinebookstore.dto.order.OrderResponseDto;
import com.example.onlinebookstore.dto.order.OrderUpdateDto;
import com.example.onlinebookstore.model.User;
import java.util.List;

public interface OrderService {
    OrderResponseDto placeOrder(User user, OrderRequestDto requestDto);

    OrderResponseDto updateOrderStatus(Long id, OrderUpdateDto updateDto);

    List<OrderResponseDto> getAllOrders(User user);
}
