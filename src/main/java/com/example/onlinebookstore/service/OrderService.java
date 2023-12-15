package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.order.OrderRequestDto;
import com.example.onlinebookstore.dto.order.OrderResponseDto;
import com.example.onlinebookstore.dto.order.OrderUpdateDto;
import java.util.List;

public interface OrderService {
    OrderResponseDto placeOrder(Long userId, OrderRequestDto requestDto);

    OrderResponseDto updateOrderStatus(Long id, OrderUpdateDto updateDto);

    List<OrderResponseDto> getAllOrders(Long userId);

    List<OrderResponseDto> getAllOrdersByStatus(String status);
}
