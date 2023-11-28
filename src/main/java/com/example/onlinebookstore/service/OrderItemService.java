package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.order.OrderItemDto;
import com.example.onlinebookstore.model.User;
import java.util.List;

public interface OrderItemService {
    List<OrderItemDto> getAllOrderItems(User user, Long orderId);

    OrderItemDto getOrderItemFromOrder(User user, Long orderId, Long orderItemId);
}
