package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.cart.AddBookRequestDto;
import com.example.onlinebookstore.dto.cart.CartResponseDto;
import com.example.onlinebookstore.dto.cartitem.CartItemDto;
import com.example.onlinebookstore.dto.cartitem.UpdateQuantityRequestDto;

public interface ShoppingCartService {
    CartResponseDto getShoppingCart(Long userId);

    CartResponseDto addBook(Long userId, AddBookRequestDto addBookRequestDto);

    CartItemDto updateCartItem(Long userId, Long id, UpdateQuantityRequestDto updateDto);

    void deleteOrder(Long userId, Long id);
}
