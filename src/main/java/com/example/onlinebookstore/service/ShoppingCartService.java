package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.cart.AddBookRequestDto;
import com.example.onlinebookstore.dto.cart.CartResponseDto;
import com.example.onlinebookstore.dto.cartitem.CartItemDto;
import com.example.onlinebookstore.dto.cartitem.UpdateQuantityRequestDto;
import com.example.onlinebookstore.model.User;

public interface ShoppingCartService {
    CartResponseDto getShoppingCart(User user);

    CartResponseDto addBook(User user, AddBookRequestDto addBookRequestDto);

    CartItemDto updateCartItem(User user, Long id, UpdateQuantityRequestDto updateDto);

    void deleteOrder(User user, Long id);
}
