package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.order.OrderItemDto;
import java.util.List;

public interface OrderItemService {
    List<OrderItemDto> getAllOrderItems(Long userId, Long orderId);

    OrderItemDto getOrderItemFromOrder(Long userId, Long orderId, Long orderItemId);
}
