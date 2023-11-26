package com.example.onlinebookstore.dto.cart;

import com.example.onlinebookstore.dto.cartitem.CartItemDto;
import java.util.List;
import lombok.Data;

@Data
public class CartResponseDto {
    private Long id;
    private Long userId;
    private List<CartItemDto> cartItems;
}
