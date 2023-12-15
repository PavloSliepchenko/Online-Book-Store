package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.dto.order.OrderRequestDto;
import com.example.onlinebookstore.dto.order.OrderResponseDto;
import com.example.onlinebookstore.dto.order.OrderUpdateDto;
import com.example.onlinebookstore.exception.EntityNotFoundException;
import com.example.onlinebookstore.mapper.OrderMapper;
import com.example.onlinebookstore.model.CartItem;
import com.example.onlinebookstore.model.Order;
import com.example.onlinebookstore.model.OrderItem;
import com.example.onlinebookstore.model.ShoppingCart;
import com.example.onlinebookstore.model.User;
import com.example.onlinebookstore.repository.CartItemRepository;
import com.example.onlinebookstore.repository.OrderItemRepository;
import com.example.onlinebookstore.repository.OrderRepository;
import com.example.onlinebookstore.repository.ShoppingCartRepository;
import com.example.onlinebookstore.repository.UserRepository;
import com.example.onlinebookstore.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponseDto placeOrder(Long userId, OrderRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("Cannot find a shopping cart of the user id "
                        + userId
                ));
        Optional<User> user = userRepository.findById(userId);

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setUser(user.get());
        order.setShippingAddress(requestDto.getShippingAddress());
        order.setStatus(Order.Status.PENDING);
        Order orderFromDb = orderRepository.save(order);

        Set<CartItem> cartItems = shoppingCart.getCartItems();
        Set<OrderItem> orderItems = getOrderItems(orderFromDb, cartItems);
        removeCartItems(cartItems);
        orderFromDb.setOrderItems(orderItems);
        return orderMapper.toDto(orderRepository.save(orderFromDb));
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long id, OrderUpdateDto updateDto) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            throw new EntityNotFoundException("There is no order with id " + id);
        }
        Order order = orderOptional.get();
        order.setStatus(Order.Status.valueOf(updateDto.getStatus()));
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public List<OrderResponseDto> getAllOrders(Long userId) {
        List<Order> allOrders = orderRepository.findAllByUserId(userId);
        return allOrders.stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderResponseDto> getAllOrdersByStatus(String statusName) {
        Order.Status status = Order.Status.valueOf(statusName.toUpperCase());
        return orderRepository.findAllByStatus(status).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    private Set<OrderItem> getOrderItems(Order order, Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(e -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setQuantity(e.getQuantity());
                    orderItem.setOrder(order);
                    orderItem.setBook(e.getBook());
                    orderItem.setPrice(BigDecimal.valueOf(
                            e.getBook().getPrice().longValue() * e.getQuantity())
                    );
                    return orderItemRepository.save(orderItem);
                })
                .collect(Collectors.toSet());
    }

    private void removeCartItems(Set<CartItem> cartItems) {
        cartItems.forEach(e -> cartItemRepository.deleteById(e.getId()));
    }
}
