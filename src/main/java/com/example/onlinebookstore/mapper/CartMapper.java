package com.example.onlinebookstore.mapper;

import com.example.onlinebookstore.config.MapperConfig;
import com.example.onlinebookstore.dto.cart.CartResponseDto;
import com.example.onlinebookstore.dto.cartitem.CartItemDto;
import com.example.onlinebookstore.model.ShoppingCart;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CartMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "cartItems", ignore = true)
    CartResponseDto toDto(ShoppingCart shoppingCart);

    @AfterMapping
    default void setCartItems(@MappingTarget CartResponseDto cartResponseDto,
                              ShoppingCart shoppingCart) {
        List<CartItemDto> cartItems = shoppingCart.getCartItems().stream()
                .map(e -> {
                    CartItemDto dto = new CartItemDto();
                    dto.setId(e.getId());
                    dto.setBookId(e.getBook().getId());
                    dto.setBookTitle(e.getBook().getTitle());
                    dto.setQuantity(e.getQuantity());
                    return dto;
                })
                .toList();
        cartResponseDto.setCartItems(cartItems);
    }
}
