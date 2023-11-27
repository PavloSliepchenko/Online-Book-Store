package com.example.onlinebookstore.controller;

import com.example.onlinebookstore.dto.cart.AddBookRequestDto;
import com.example.onlinebookstore.dto.cart.CartResponseDto;
import com.example.onlinebookstore.dto.cartitem.CartItemDto;
import com.example.onlinebookstore.dto.cartitem.UpdateQuantityRequestDto;
import com.example.onlinebookstore.model.User;
import com.example.onlinebookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/cart")
@Tag(name = "Carts management", description = "Endpoints for CRUD operations with carts")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Get a cart", description = "Provides user's cart content")
    public CartResponseDto getCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getShoppingCart(user);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Add a book to the cart", description = "Adds a book order to the cart")
    public CartResponseDto addBook(@RequestBody @Valid AddBookRequestDto addBookRequestDto,
                                   Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.addBook(user, addBookRequestDto);
    }

    @PutMapping(value = "/cart-items/{id}")
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Update order", description = "Updates a number of certain book ordered")
    public CartItemDto updateOrder(
            @RequestBody UpdateQuantityRequestDto updateDto,
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.updateCartItem(user, id, updateDto);
    }

    @DeleteMapping(value = "/cart-items/{id}")
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Delete an order",
            description = "Removes an order by id using a soft delete")
    public void deleteOrder(Authentication authentication, @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();
        shoppingCartService.deleteOrder(user, id);
    }
}
