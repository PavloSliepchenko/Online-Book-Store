package com.example.onlinebookstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.onlinebookstore.dto.cart.AddBookRequestDto;
import com.example.onlinebookstore.dto.cart.CartResponseDto;
import com.example.onlinebookstore.dto.cartitem.CartItemDto;
import com.example.onlinebookstore.dto.cartitem.UpdateQuantityRequestDto;
import com.example.onlinebookstore.repository.CartItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CartItemRepository cartItemRepository;

    @BeforeAll
    static void setUp(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Get user's cart")
    @Sql(scripts = {
            "classpath:database/books/add-users.sql",
            "classpath:database/books/add-shopping_carts.sql",
            "classpath:database/books/set-roles-to-users.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/clear-users_roles-table.sql",
            "classpath:database/books/clear-shopping_carts-table.sql",
            "classpath:database/books/clear-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("second@user.com")
    public void getCart_ValidRequest_ShouldReturnCartResponseDto() throws Exception {
        CartResponseDto expected = new CartResponseDto();
        expected.setId(1L);
        expected.setUserId(2L);

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.userId").value(expected.getUserId()));
    }

    @Test
    @DisplayName("Add a book to the cart")
    @Sql(scripts = {
            "classpath:database/books/add-users.sql",
            "classpath:database/books/add-shopping_carts.sql",
            "classpath:database/books/set-roles-to-users.sql",
            "classpath:database/books/add-three-books.sql",
            "classpath:database/books/add-cart-items.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/clear-users_roles-table.sql",
            "classpath:database/books/clear-shopping_carts-table.sql",
            "classpath:database/books/clear-users-table.sql",
            "classpath:database/books/clear-books-table.sql",
            "classpath:database/books/clear-cart_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("fourth@user.com")
    public void addBook_ValidRequestParams_ShouldReturnCartResponseDto() throws Exception {
        Long bookId = 2L;
        AddBookRequestDto addBookRequestDto = new AddBookRequestDto();
        addBookRequestDto.setBookId(bookId);
        addBookRequestDto.setQuantity(3);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setBookTitle("Head First Java");
        cartItemDto.setBookId(bookId);
        cartItemDto.setQuantity(addBookRequestDto.getQuantity());
        cartItemDto.setId(4L);

        CartResponseDto expected = new CartResponseDto();
        expected.setUserId(4L);
        expected.setId(3L);
        expected.setCartItems(List.of(cartItemDto));

        String requestJson = objectMapper.writeValueAsString(addBookRequestDto);
        mockMvc.perform(post("/api/cart")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.userId").value(expected.getUserId()))
                .andExpect(jsonPath("$.cartItems[0].id")
                        .value(expected.getCartItems().get(0).getId()))
                .andExpect(jsonPath("$.cartItems[0].bookId")
                        .value(expected.getCartItems().get(0).getBookId()))
                .andExpect(jsonPath("$.cartItems[0].bookTitle")
                        .value(expected.getCartItems().get(0).getBookTitle()));
    }

    @Test
    @DisplayName("Update an existing order")
    @Sql(scripts = {
            "classpath:database/books/add-users.sql",
            "classpath:database/books/add-shopping_carts.sql",
            "classpath:database/books/set-roles-to-users.sql",
            "classpath:database/books/add-three-books.sql",
            "classpath:database/books/add-cart-items.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/clear-users_roles-table.sql",
            "classpath:database/books/clear-shopping_carts-table.sql",
            "classpath:database/books/clear-users-table.sql",
            "classpath:database/books/clear-books-table.sql",
            "classpath:database/books/clear-cart_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("third@user.com")
    public void updateOrder_ValidUpdateRequest_ShouldReturnCartItemDto() throws Exception {
        UpdateQuantityRequestDto updateQuantityRequestDto = new UpdateQuantityRequestDto();
        updateQuantityRequestDto.setQuantity(21);

        CartItemDto expected = new CartItemDto();
        expected.setId(2L);
        expected.setQuantity(updateQuantityRequestDto.getQuantity());
        expected.setBookId(1L);
        expected.setBookTitle("Java");

        String requestJson = objectMapper.writeValueAsString(updateQuantityRequestDto);
        mockMvc.perform(put("/api/cart/cart-items/" + expected.getId())
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.bookId").value(expected.getBookId()))
                .andExpect(jsonPath("$.quantity").value(expected.getQuantity()))
                .andExpect(jsonPath("$.bookTitle").value(expected.getBookTitle()));
    }

    @Test
    @DisplayName("Delete a cart item")
    @Sql(scripts = {
            "classpath:database/books/add-users.sql",
            "classpath:database/books/add-shopping_carts.sql",
            "classpath:database/books/set-roles-to-users.sql",
            "classpath:database/books/add-three-books.sql",
            "classpath:database/books/add-cart-items.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/clear-users_roles-table.sql",
            "classpath:database/books/clear-shopping_carts-table.sql",
            "classpath:database/books/clear-users-table.sql",
            "classpath:database/books/clear-books-table.sql",
            "classpath:database/books/clear-cart_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("third@user.com")
    public void deleteOrder_ValidItemId_ShouldRemoveOrder() throws Exception {
        Long cartItemId = 3L;
        Assertions.assertTrue(cartItemRepository.existsById(cartItemId),
                "There is no cart item with id " + cartItemId);

        mockMvc.perform(delete("/api/cart/cart-items/" + cartItemId))
                .andExpect(status().isOk());

        Assertions.assertFalse(cartItemRepository.existsById(cartItemId),
                "Cart item wasn't deleted. There is a cart item by id " + cartItemId);
    }
}
