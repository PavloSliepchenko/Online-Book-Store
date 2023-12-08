package com.example.onlinebookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.onlinebookstore.dto.book.BookDto;
import com.example.onlinebookstore.dto.book.CreateBookRequestDto;
import com.example.onlinebookstore.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import java.math.BigDecimal;
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
class BookControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookRepository bookRepository;

    @BeforeAll
    static void setUp(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Get all books")
    @WithMockUser(username = "user", authorities = "USER")
    @Sql(scripts = "classpath:database/books/add-three-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/clear-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAll_ValidRequest_ShouldReturnListOfAllBooksDtos() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        List<BookDto> actual = List.of(objectMapper.readValue(contentAsString, BookDto[].class));
        Assertions.assertEquals(3, actual.size());
        for (BookDto dto: actual) {
            Assertions.assertNotNull(dto);
            assertThat(dto.getId()).isIn(1L, 2L, 3L);
            assertThat(dto.getIsbn()).isIn(
                    "java1234",
                    "Head First Java 1234", 
                    "Java for Dummies 1234"
            );
        }
    }

    @Test
    @DisplayName("Get a book by id")
    @WithMockUser(username = "user", authorities = "USER")
    @Sql(scripts = "classpath:database/books/add-three-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/clear-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getBookById_ValidBookId_ShouldReturnCorrectBook() throws Exception {
        Long bookId = 1L;
        MvcResult result = mockMvc.perform(get("/api/books/" + bookId))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertEquals(bookId, actual.getId());
        Assertions.assertEquals("Java", actual.getTitle());

        bookId = 3L;
        result = mockMvc.perform(get("/api/books/" + bookId))
                .andExpect(status().isOk())
                .andReturn();
        actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertEquals(bookId, actual.getId());
        Assertions.assertEquals("Java for Dummies", actual.getTitle());
    }

    @Test
    @DisplayName("Add a new book to DB")
    @WithMockUser(username = "Admin", authorities = "ADMIN")
    @Sql(scripts = "classpath:database/books/add-three-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/clear-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createBook_ValidCreateBookRequest_ShouldReturnValidBookDto() throws Exception {
        CreateBookRequestDto createBookDto = getCreateBookRequestDto();
        String jasonObject = objectMapper.writeValueAsString(createBookDto);
        MvcResult result = mockMvc.perform(post("/api/books")
                        .content(jasonObject)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        BookDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertEquals(actual.getId(), 4L);
        Assertions.assertEquals(createBookDto.getTitle(), actual.getTitle());
        Assertions.assertEquals(createBookDto.getIsbn(), actual.getIsbn());
        Assertions.assertEquals(createBookDto.getDescription(), actual.getDescription());
    }

    @Test
    @DisplayName("Delete book by id")
    @WithMockUser(username = "Admin", authorities = {"ADMIN", "USER"})
    @Sql(scripts = "classpath:database/books/add-three-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/clear-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteById_ValidBookId_ShouldDeleteBook() throws Exception {
        Long bookId = 1L;
        Assertions.assertTrue(bookRepository.existsById(bookId),
                "There is no book with id " + bookId + " in DB");
        MvcResult result = mockMvc.perform(delete("/api/books/" + bookId))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertThrows(ServletException.class,
                () -> mockMvc.perform(get("/api/books/" + bookId)));
    }

    @Test
    @DisplayName("Update an existing book")
    @WithMockUser(username = "Admin", authorities = "ADMIN")
    @Sql(scripts = "classpath:database/books/add-three-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/clear-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void update_ValidUpdateRequest_ShouldReturnBookDtoOfUpdatedBook() throws Exception {
        Long bookId = 1L;
        CreateBookRequestDto createBookRequestDto = getCreateBookRequestDto();
        String requestJson = objectMapper.writeValueAsString(createBookRequestDto);
        MvcResult result = mockMvc.perform(put("/api/books/" + bookId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        BookDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertEquals(bookId, actual.getId());
        Assertions.assertEquals(createBookRequestDto.getTitle(), actual.getTitle());
        Assertions.assertEquals(createBookRequestDto.getPrice(), actual.getPrice());
        Assertions.assertEquals(createBookRequestDto.getDescription(), actual.getDescription());
        Assertions.assertEquals(createBookRequestDto.getIsbn(), actual.getIsbn());
    }

    @Test
    @DisplayName("Search books by parameters")
    @WithMockUser(username = "User", authorities = "USER")
    @Sql(scripts = "classpath:database/books/add-three-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/clear-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void searchBooks_ValidSearchRequest_ShouldReturnListOfBookDtos() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/books/search?titles=Java"))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> bookDtos = List.of(objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto[].class));
        Assertions.assertEquals(1L, bookDtos.get(0).getId());
        Assertions.assertEquals("java1234", bookDtos.get(0).getIsbn());

        result = mockMvc.perform(get("/api/books/search?isbns=Head First Java 1234"))
                .andExpect(status().isOk())
                .andReturn();
        bookDtos = List.of(objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto[].class));
        bookDtos.forEach(System.out::println);
        Assertions.assertEquals(2L, bookDtos.get(0).getId());
        Assertions.assertEquals("Head First Java", bookDtos.get(0).getTitle());
    }

    private CreateBookRequestDto getCreateBookRequestDto() {
        CreateBookRequestDto createBookDto = new CreateBookRequestDto();
        createBookDto.setTitle("A new book");
        createBookDto.setIsbn("12345902");
        createBookDto.setPrice(BigDecimal.valueOf(231));
        createBookDto.setDescription("A great new story book");
        return createBookDto;
    }
}
