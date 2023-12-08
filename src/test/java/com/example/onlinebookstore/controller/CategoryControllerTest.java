package com.example.onlinebookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.onlinebookstore.dto.category.CategoryResponseDto;
import com.example.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.example.onlinebookstore.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeAll
    static void setUp(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Add new category")
    @WithMockUser(username = "Admin", authorities = "ADMIN")
    @Sql(scripts = "classpath:database/books/add-two-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/clear-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createCategory_ValidRequest_ShouldReturnCategoryResponseDto() throws Exception {
        CreateCategoryRequestDto requestDto = getRequestDto();
        String requestJson = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/api/categories")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        CategoryResponseDto.class);

        Assertions.assertEquals(3L, actual.getId());
        Assertions.assertEquals(requestDto.getName(), actual.getName());
        Assertions.assertEquals(requestDto.getDescription(), actual.getDescription());
    }

    @Test
    @DisplayName("Get all categories")
    @WithMockUser(username = "user", authorities = "USER")
    @Sql(scripts = "classpath:database/books/add-two-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/clear-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAll_ValidRequest_ShouldReturnListOfCategoryDtos() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryResponseDto> categories =
                List.of(objectMapper.readValue(result.getResponse().getContentAsString(),
                        CategoryResponseDto[].class));

        Assertions.assertEquals(2, categories.size());
        for (CategoryResponseDto category : categories) {
            assertThat(category.getId()).isIn(1L, 2L);
            assertThat(category.getName()).isIn("Education", "Fiction");
        }
    }

    @Test
    @DisplayName("Find category by id")
    @WithMockUser(username = "user", authorities = "USER")
    @Sql(scripts = "classpath:database/books/add-two-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/clear-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getCategoryById_ValidRequest_ShouldReturnCorrectCategory() throws Exception {
        Long categoryId = 2L;
        MvcResult result = mockMvc.perform(get("/api/categories/" + categoryId))
                .andExpect(status().isOk())
                .andReturn();

        CategoryResponseDto responseDto =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        CategoryResponseDto.class);

        Assertions.assertEquals(categoryId, responseDto.getId());
        Assertions.assertEquals("Fiction", responseDto.getName());
    }

    @Test
    @DisplayName("Update an existing category")
    @WithMockUser(username = "Admin", authorities = "ADMIN")
    @Sql(scripts = "classpath:database/books/add-two-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/clear-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateCategory_ValidRequestParams_ShouldReturnUpdatedCategory() throws Exception {
        CreateCategoryRequestDto requestDto = getRequestDto();
        String requestJson = objectMapper.writeValueAsString(requestDto);
        Long categoryId = 1L;

        MvcResult result = mockMvc.perform(put("/api/categories/" + categoryId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryResponseDto responseDto =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        CategoryResponseDto.class);

        Assertions.assertEquals(categoryId, responseDto.getId());
        Assertions.assertEquals(requestDto.getName(), responseDto.getName());
        Assertions.assertEquals(requestDto.getDescription(), responseDto.getDescription());
    }

    @Test
    @DisplayName("Delete category by its id")
    @WithMockUser(username = "Admin", authorities = "ADMIN")
    @Sql(scripts = "classpath:database/books/add-two-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/clear-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteCategory_CorrectCategoryId_ShouldDeleteCategory() throws Exception {
        Long categoryId = 2L;
        Assertions.assertTrue(categoryRepository.existsById(categoryId),
                "There is no category with id " + categoryId + " in DB");

        MvcResult result = mockMvc.perform(delete("/api/categories/" + categoryId))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertFalse(categoryRepository.existsById(categoryId),
                "There is a category with id " + categoryId + " in DB. Not removed!");
    }

    @Test
    @DisplayName("Delete category by its id")
    @WithMockUser(username = "User", authorities = "USER")
    @Sql(scripts = {
            "classpath:database/books/add-two-categories.sql",
            "classpath:database/books/add-three-books.sql",
            "classpath:database/books/fill-in-books_categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/clear-categories-table.sql",
            "classpath:database/books/clear-books-table.sql",
            "classpath:database/books/clear-books_categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getBooksByCategoryId_ValidRequest_ShouldReturnBookDtoWithoutCategoryIds()
            throws Exception {
        Long categoryId = 1L;
        MvcResult result = mockMvc.perform(get("/api/categories/" + categoryId + "/books"))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDtoWithoutCategoryIds> responseList =
                List.of(objectMapper.readValue(result.getResponse().getContentAsString(),
                        BookDtoWithoutCategoryIds[].class));

        Assertions.assertEquals(2, responseList.size());
        for (BookDtoWithoutCategoryIds actual : responseList) {
            assertThat(actual.getId()).isIn(1L, 2L);
            assertThat(actual.getTitle()).isIn("Java", "Head First Java");
        }

        categoryId = 2L;
        result = mockMvc.perform(get("/api/categories/" + categoryId + "/books"))
                .andExpect(status().isOk())
                .andReturn();

        responseList = List.of(objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDtoWithoutCategoryIds[].class));
        Assertions.assertEquals(1, responseList.size());
        Assertions.assertEquals(3L, responseList.get(0).getId());
        Assertions.assertEquals("Java for Dummies", responseList.get(0).getTitle());
    }

    private CreateCategoryRequestDto getRequestDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Romance");
        requestDto.setDescription("its primary focus on the relationship "
                + "and romantic love between two people");
        return requestDto;
    }
}
