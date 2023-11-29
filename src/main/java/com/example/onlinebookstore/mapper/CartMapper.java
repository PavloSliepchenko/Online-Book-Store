package com.example.onlinebookstore.mapper;

import com.example.onlinebookstore.config.MapperConfig;
import com.example.onlinebookstore.dto.cart.CartResponseDto;
import com.example.onlinebookstore.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface CartMapper {
    @Mapping(target = "userId", source = "user.id")
    CartResponseDto toDto(ShoppingCart shoppingCart);
}
