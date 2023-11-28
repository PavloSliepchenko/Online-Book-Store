package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.dto.order.OrderItemDto;
import com.example.onlinebookstore.exception.EntityNotFoundException;
import com.example.onlinebookstore.mapper.OrderItemMapper;
import com.example.onlinebookstore.model.Order;
import com.example.onlinebookstore.model.OrderItem;
import com.example.onlinebookstore.repository.OrderItemRepository;
import com.example.onlinebookstore.repository.OrderRepository;
import com.example.onlinebookstore.service.OrderItemService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItemDto> getAllOrderItems(Long userId, Long orderId) {
        Order order = getOrder(userId, orderId);

        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto getOrderItemFromOrder(Long userId, Long orderId, Long orderItemId) {
        Order order = getOrder(userId, orderId);
        Optional<OrderItem> orderItem =
                orderItemRepository.findByIdAndOrderId(orderItemId, orderId);

        if (orderItem.isEmpty()) {
            throw new EntityNotFoundException("User with id " + userId
                            + " doesn't have an order item with id " + orderItemId);
        }

        return orderItemMapper.toDto(orderItem.get());
    }

    private Order getOrder(Long userId, Long orderId) {
        return orderRepository.findByIdAndUserId(orderId, userId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Cannot find an order with id "
                                + orderId
                                + " for user with id "
                                + userId)
        );
    }
}
