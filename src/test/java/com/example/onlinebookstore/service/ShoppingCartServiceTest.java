package com.example.onlinebookstore.service;

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
import com.example.onlinebookstore.model.User;
import com.example.onlinebookstore.repository.BookRepository;
import com.example.onlinebookstore.repository.CartItemRepository;
import com.example.onlinebookstore.repository.ShoppingCartRepository;
import com.example.onlinebookstore.service.impl.ShoppingCartServiceImpl;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    @Mock
    private ShoppingCartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private CartMapper cartMapper;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("Get shopping cart by user id")
    public void getShoppingCart_ValidUserId_ShouldReturnCartResponseDto() {
        User user = getUser(1L);
        ShoppingCart shoppingCart = getShoppingCart(user);
        CartResponseDto expected = getCartResponseDto(shoppingCart);

        Mockito.when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        Mockito.when(cartMapper.toDto(shoppingCart)).thenReturn(expected);

        CartResponseDto actual = shoppingCartService.getShoppingCart(user.getId());
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getUserId(), actual.getUserId());
    }

    @Test
    @DisplayName("Get shopping cart by user id. Throws an exception")
    public void getShoppingCart_WrongUserId_ShouldThrowException() {
        Mockito.when(cartRepository.findByUserId(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.getShoppingCart(2L));
    }

    @Test
    @DisplayName("Add book to cart")
    public void addBook_ValidRequest_ShouldReturnCartResponseDto() {
        Book book = getBook();
        AddBookRequestDto addBookRequestDto = new AddBookRequestDto();
        addBookRequestDto.setBookId(book.getId());
        addBookRequestDto.setQuantity(2);

        User user = getUser(1L);
        ShoppingCart shoppingCart = getShoppingCart(user);

        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItem.setQuantity(addBookRequestDto.getQuantity());

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setBookTitle(cartItem.getBook().getTitle());
        cartItemDto.setBookId(cartItem.getBook().getId());
        cartItemDto.setQuantity(cartItem.getQuantity());

        CartResponseDto expected = getCartResponseDto(shoppingCart);
        expected.setCartItems(List.of(cartItemDto));

        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(cartRepository.save(shoppingCart)).thenReturn(shoppingCart);
        Mockito.when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        Mockito.when(cartMapper.toDto(shoppingCart)).thenReturn(expected);
        Mockito.when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));

        CartResponseDto actual = shoppingCartService.addBook(user.getId(), addBookRequestDto);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getUserId(), actual.getUserId());
        Assertions.assertEquals(expected.getCartItems().size(), actual.getCartItems().size());
        Assertions.assertEquals(expected.getCartItems().get(0).getId(),
                actual.getCartItems().get(0).getId());
    }

    @Test
    @DisplayName("Add book to cart. Throws exception")
    public void addBook_WrongBookId_ShouldThrowException() {
        User user = getUser(1L);
        ShoppingCart shoppingCart = getShoppingCart(user);
        AddBookRequestDto addBookRequestDto = new AddBookRequestDto();
        addBookRequestDto.setBookId(2L);
        Mockito.when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        Mockito.when(bookRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addBook(user.getId(), addBookRequestDto));
    }

    @Test
    @DisplayName("Update cart item")
    public void updateCartItem_ValidRequest_ShouldReturnCartItemDto() {
        User user = getUser(2L);

        CartItem cartItem = new CartItem();
        cartItem.setId(3L);
        cartItem.setBook(getBook());
        cartItem.setQuantity(5);

        ShoppingCart shoppingCart = getShoppingCart(user);
        shoppingCart.setCartItems(Set.of(cartItem));

        UpdateQuantityRequestDto updateQuantityRequestDto = new UpdateQuantityRequestDto();
        updateQuantityRequestDto.setQuantity(15);

        CartItemDto expected = new CartItemDto();
        expected.setId(cartItem.getId());
        expected.setBookId(cartItem.getBook().getId());
        expected.setBookTitle(cartItem.getBook().getTitle());
        expected.setQuantity(updateQuantityRequestDto.getQuantity());

        Mockito.when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        Mockito.when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        Mockito.when(cartItemMapper.toDto(cartItem)).thenReturn(expected);

        CartItemDto actual = shoppingCartService.updateCartItem(
                user.getId(),
                cartItem.getId(),
                updateQuantityRequestDto
        );

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getBookId(), actual.getBookId());
        Assertions.assertEquals(expected.getQuantity(), actual.getQuantity());
        Assertions.assertEquals(expected.getBookTitle(), actual.getBookTitle());
    }

    @Test
    @DisplayName("Update cart item. Throws an exception")
    public void updateCartItem_WrongItemId_ShouldThrowException() {
        User user = getUser(2L);
        ShoppingCart shoppingCart = getShoppingCart(user);
        shoppingCart.setCartItems(new HashSet<>());
        UpdateQuantityRequestDto quantityRequestDto = new UpdateQuantityRequestDto();
        quantityRequestDto.setQuantity(15);

        Mockito.when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));

        Assertions.assertThrows(RuntimeException.class,
                () -> shoppingCartService.updateCartItem(user.getId(), 2L, quantityRequestDto));
    }

    private User getUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setEmail("user " + userId + "@user.com");
        user.setFirstName("Jack" + userId);
        user.setLastName("Sparrow" + userId);
        return user;
    }

    private ShoppingCart getShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(user.getId());
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());
        return shoppingCart;
    }

    private CartResponseDto getCartResponseDto(ShoppingCart shoppingCart) {
        CartResponseDto cartResponseDto = new CartResponseDto();
        cartResponseDto.setUserId(shoppingCart.getUser().getId());
        cartResponseDto.setId(shoppingCart.getId());
        return cartResponseDto;
    }

    private Book getBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Best book ever");
        book.setIsbn("01239");
        book.setPrice(BigDecimal.valueOf(231));
        return book;
    }
}
