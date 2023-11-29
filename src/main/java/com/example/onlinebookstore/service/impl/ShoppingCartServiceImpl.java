package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.dto.cart.AddBookRequestDto;
import com.example.onlinebookstore.dto.cart.CartResponseDto;
import com.example.onlinebookstore.dto.cartitem.CartItemDto;
import com.example.onlinebookstore.dto.cartitem.UpdateQuantityRequestDto;
import com.example.onlinebookstore.exception.EntityNotFoundException;
import com.example.onlinebookstore.mapper.CartItemMapper;
import com.example.onlinebookstore.mapper.CartMapper;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.model.CartItem;
import com.example.onlinebookstore.model.ShoppingCart;
import com.example.onlinebookstore.repository.BookRepository;
import com.example.onlinebookstore.repository.CartItemRepository;
import com.example.onlinebookstore.repository.ShoppingCartRepository;
import com.example.onlinebookstore.service.ShoppingCartService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final CartItemMapper cartItemMapper;
    private final CartMapper cartMapper;

    @Override
    public CartResponseDto getShoppingCart(Long userId) {
        return cartMapper.toDto(getShoppingCartByUserId(userId));
    }

    @Override
    public CartResponseDto addBook(Long userId, AddBookRequestDto addBookRequestDto) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);

        Book book = bookRepository.findById(addBookRequestDto.getBookId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Cannot find a book by id "
                                + addBookRequestDto.getBookId()));

        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItem.setQuantity(addBookRequestDto.getQuantity());

        CartItem cartItemFromDb = cartItemRepository.save(cartItem);

        shoppingCart.getCartItems().add(cartItemFromDb);
        return cartMapper.toDto(cartRepository.save(shoppingCart));
    }

    @Override
    public CartItemDto updateCartItem(Long userId, Long id, UpdateQuantityRequestDto updateDto) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        Optional<CartItem> presentItem = getCartItemById(shoppingCart, id);
        if (presentItem.isEmpty()) {
            throw new RuntimeException("You don't have an order with id " + id);
        }
        CartItem cartItem = presentItem.get();
        cartItem.setQuantity(updateDto.getQuantity());
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void deleteOrder(Long userId, Long id) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        Optional<CartItem> presentItem = getCartItemById(shoppingCart, id);
        if (presentItem.isEmpty()) {
            throw new RuntimeException("You don't have an order with id " + id);
        }
        cartItemRepository.deleteById(id);
    }

    private ShoppingCart getShoppingCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("Cannot find a shopping cart of the user id "
                        + userId
                ));
    }

    private Optional<CartItem> getCartItemById(ShoppingCart shoppingCart, Long id) {
        return shoppingCart.getCartItems().stream()
                .filter(e -> e.getId() == id)
                .findFirst();
    }
}
