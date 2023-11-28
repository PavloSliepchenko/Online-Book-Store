package com.example.onlinebookstore.controller;

import com.example.onlinebookstore.dto.order.OrderItemDto;
import com.example.onlinebookstore.dto.order.OrderRequestDto;
import com.example.onlinebookstore.dto.order.OrderResponseDto;
import com.example.onlinebookstore.dto.order.OrderUpdateDto;
import com.example.onlinebookstore.model.User;
import com.example.onlinebookstore.service.OrderItemService;
import com.example.onlinebookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/orders")
@Tag(name = "Orders management", description = "Endpoints for CRUD operations with books")
public class OrderController {
    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Place an order", description = "Sets an order")
    public OrderResponseDto placeOrder(Authentication authentication,
                                       @RequestBody OrderRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return orderService.placeOrder(user.getId(), requestDto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "View orders", description = "Retrieves user's order history")
    List<OrderResponseDto> getAllOrders(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.getAllOrders(user.getId());
    }

    @PatchMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update status", description = "Updates order status")
    OrderResponseDto updateStatus(@PathVariable Long id,
                                  @RequestBody OrderUpdateDto updateDto) {
        return orderService.updateOrderStatus(id, updateDto);
    }

    @GetMapping(value = "/{orderId}/items")
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Get order items",
            description = "Retrieves all order items for a specific order")
    List<OrderItemDto> getAllOrderItems(Authentication authentication, @PathVariable Long orderId) {
        User user = (User) authentication.getPrincipal();
        return orderItemService.getAllOrderItems(user.getId(), orderId);
    }

    @GetMapping(value = "/{orderId}/items/{itemId}")
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Get an order item from order",
            description = "Retrieves a specific order item within an order")
    OrderItemDto getOrderItemFromOrder(Authentication authentication,
                                       @PathVariable Long orderId,
                                       @PathVariable Long itemId) {
        User user = (User) authentication.getPrincipal();
        return orderItemService.getOrderItemFromOrder(user.getId(), orderId, itemId);
    }
}
