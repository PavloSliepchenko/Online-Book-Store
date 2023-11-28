package com.example.onlinebookstore.mapper;

import com.example.onlinebookstore.config.MapperConfig;
import com.example.onlinebookstore.dto.order.OrderResponseDto;
import com.example.onlinebookstore.model.Order;
import com.example.onlinebookstore.model.OrderItem;
import java.math.BigDecimal;
import java.util.Optional;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "total", ignore = true)
    OrderResponseDto toDto(Order order);

    @AfterMapping
    default void setTotalAmount(@MappingTarget OrderResponseDto responseDto,
                                             Order order) {
        Optional<BigDecimal> sum = order.getOrderItems().stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal::add);
        responseDto.setTotal(sum.get());
    }
}
