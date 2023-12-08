package com.example.onlinebookstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.onlinebookstore.dto.cart.AddBookRequestDto;
import com.example.onlinebookstore.dto.cart.CartResponseDto;
import com.example.onlinebookstore.dto.cartitem.CartItemDto;
import com.example.onlinebookstore.dto.cartitem.UpdateQuantityRequestDto;
import com.example.onlinebookstore.repository.CartItemRepository;
import com.example.onlinebookstore.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;
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
    public void getCart_ValidRequest_ShouldReturnCartResponseDto() throws Exception {
        CartResponseDto expected = new CartResponseDto();
        expected.setId(1L);
        expected.setUserId(2L);

        String token = getToken("second@user.com");
        MvcResult result = mockMvc.perform(get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andReturn();
        CartResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        CartResponseDto.class);

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getUserId(), actual.getUserId());
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
        String token = getToken("fourth@user.com");
        MvcResult result = mockMvc.perform(post("/api/cart")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andReturn();

        CartResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        CartResponseDto.class);

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getUserId(), actual.getUserId());
        Assertions.assertEquals(expected.getCartItems().get(0).getId(),
                actual.getCartItems().get(0).getId());
        Assertions.assertEquals(expected.getCartItems().get(0).getBookId(),
                actual.getCartItems().get(0).getBookId());
        Assertions.assertEquals(expected.getCartItems().get(0).getBookTitle(),
                actual.getCartItems().get(0).getBookTitle());
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
    public void updateOrder_ValidUpdateRequest_ShouldReturnCartItemDto() throws Exception {
        UpdateQuantityRequestDto updateQuantityRequestDto = new UpdateQuantityRequestDto();
        updateQuantityRequestDto.setQuantity(21);

        CartItemDto expected = new CartItemDto();
        expected.setId(2L);
        expected.setQuantity(updateQuantityRequestDto.getQuantity());
        expected.setBookId(1L);
        expected.setBookTitle("Java");

        String requestJson = objectMapper.writeValueAsString(updateQuantityRequestDto);
        String token = getToken("third@user.com");
        MvcResult result = mockMvc.perform(put("/api/cart/cart-items/" + expected.getId())
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andReturn();

        CartItemDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        CartItemDto.class);

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getBookId(), actual.getBookId());
        Assertions.assertEquals(expected.getQuantity(), actual.getQuantity());
        Assertions.assertEquals(expected.getBookTitle(), actual.getBookTitle());
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
    public void deleteOrder_ValidItemId_ShouldRemoveOrder() throws Exception {
        Long cartItemId = 3L;
        Assertions.assertTrue(cartItemRepository.existsById(cartItemId),
                "There is no cart item with id " + cartItemId);

        String token = getToken("third@user.com");
        MvcResult result = mockMvc.perform(delete("/api/cart/cart-items/" + cartItemId)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertFalse(cartItemRepository.existsById(cartItemId),
                "Cart item wasn't deleted. There is a cart item by id " + cartItemId);
    }

    private String getToken(String email) {
        return "Bearer " + jwtUtil.generateToken(email);
    }
}
