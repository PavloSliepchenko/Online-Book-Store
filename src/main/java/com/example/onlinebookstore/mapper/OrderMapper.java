package com.example.onlinebookstore.mapper;

import com.example.onlinebookstore.config.MapperConfig;
import com.example.onlinebookstore.dto.order.OrderItemDto;
import com.example.onlinebookstore.dto.order.OrderResponseDto;
import com.example.onlinebookstore.model.Order;
import com.example.onlinebookstore.model.OrderItem;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "total", ignore = true)
    OrderResponseDto toDto(Order order);

    @AfterMapping
    default void setOrderItemsAndTotalAmount(@MappingTarget OrderResponseDto responseDto,
                                             Order order) {
        List<OrderItemDto> orderItems = order.getOrderItems().stream()
                .map(e -> {
                    OrderItemDto orderItemDto = new OrderItemDto();
                    orderItemDto.setId(e.getId());
                    orderItemDto.setBookId(e.getBook().getId());
                    orderItemDto.setQuantity(e.getQuantity());
                    return orderItemDto;
                })
                .toList();

        Optional<BigDecimal> sum = order.getOrderItems().stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal::add);

        responseDto.setOrderItems(orderItems);
        responseDto.setTotal(sum.get());
    }
}
