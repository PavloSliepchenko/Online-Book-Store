package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.book.BookDto;
import com.example.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.onlinebookstore.dto.book.BookSearchParametersDto;
import com.example.onlinebookstore.dto.book.CreateBookRequestDto;
import com.example.onlinebookstore.exception.EntityNotFoundException;
import com.example.onlinebookstore.mapper.BookMapper;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.repository.BookRepository;
import com.example.onlinebookstore.repository.BookSpecificationBuilder;
import com.example.onlinebookstore.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    private Book book;
    private Book book2;
    private Book book3;
    private BookDto bookDto;
    private BookDto book2Dto;
    private BookDto book3Dto;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    private void initBookAndBookDto() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Java");
        book.setIsbn("java1234");
        book.setDescription("A Beginner's Guide, Eighth Edition");
        book.setPrice(BigDecimal.valueOf(23.95));

        book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Head First Java");
        book2.setIsbn("Head First Java 1234");
        book2.setDescription("Head First Java is referred to as the Java programming bible");
        book2.setPrice(BigDecimal.valueOf(38.50));

        book3 = new Book();
        book3.setId(3L);
        book3.setTitle("Java for Dummies");
        book3.setIsbn("Java for Dummies 1234");
        book3.setDescription("A great beginner’s guide to Java programming");
        book3.setPrice(BigDecimal.valueOf(48));

        bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());

        book2Dto = new BookDto();
        book2Dto.setId(book2.getId());
        book2Dto.setTitle(book2.getTitle());
        book2Dto.setIsbn(book2.getIsbn());
        book2Dto.setPrice(book2.getPrice());
        book2Dto.setDescription(book2.getDescription());

        book3Dto = new BookDto();
        book3Dto.setId(book3.getId());
        book3Dto.setTitle(book3.getTitle());
        book3Dto.setIsbn(book3.getIsbn());
        book3Dto.setPrice(book3.getPrice());
        book3Dto.setDescription(book3.getDescription());
    }

    @Test
    @DisplayName("Save a book to DB")
    void save_ValidCreateRequestDto_ShouldReturnValidBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(book.getTitle());
        requestDto.setIsbn(book.getIsbn());
        requestDto.setDescription(book.getDescription());
        requestDto.setPrice(book.getPrice());

        Book bookToSave = new Book();
        bookToSave.setTitle(book.getTitle());
        bookToSave.setIsbn(book.getIsbn());
        bookToSave.setDescription(book.getDescription());
        bookToSave.setPrice(book.getPrice());

        BookDto expected = bookDto;

        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);
        Mockito.when(bookRepository.save(bookToSave)).thenReturn(book);
        Mockito.when(bookMapper.toModel(requestDto)).thenReturn(bookToSave);

        BookDto actual = bookService.save(requestDto);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getIsbn(), actual.getIsbn());
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getPrice(), actual.getPrice());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    @DisplayName("Find all books")
    public void findAll_ValidRequest_ShouldReturnListOfBookDtos() {
        List<Book> books = List.of(book, book2, book3);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        Mockito.when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);
        Mockito.when(bookMapper.toDto(book2)).thenReturn(book2Dto);
        Mockito.when(bookMapper.toDto(book3)).thenReturn(book3Dto);

        List<BookDto> expected = List.of(bookDto, book2Dto, book3Dto);
        List<BookDto> actual = bookService.findAll(pageable);
        Assertions.assertTrue(actual.containsAll(expected));
        Assertions.assertEquals(expected.size(), actual.size());
    }

    @Test
    @DisplayName("Find a book by book id")
    public void getById_CorrectBookId_ShouldReturnCorrectBookDto() {
        Long bookId = 1L;
        BookDto expected = bookDto;
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);
        BookDto actual = bookService.getById(bookId);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getIsbn(), actual.getIsbn());
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getPrice(), actual.getPrice());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    @DisplayName("Find a book by book id. Throws an exception for the wrong book id")
    public void getById_WrongId_ShouldThrowException() {
        Mockito.when(bookRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.getById(Mockito.anyLong()));
    }

    @Test
    @DisplayName("Update an existing book")
    public void update_ValidUpdateRequest_ShouldUpdateBook() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(book.getTitle());
        requestDto.setIsbn(book.getIsbn());
        requestDto.setDescription(book.getDescription());
        requestDto.setPrice(book.getPrice());

        BookDto expected = bookDto;
        Mockito.when(bookMapper.toModel(requestDto)).thenReturn(book);
        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(bookRepository.existsById(Mockito.anyLong())).thenReturn(true);

        Long bookId = book.getId();
        BookDto actual = bookService.update(bookId, requestDto);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getIsbn(), actual.getIsbn());
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getPrice(), actual.getPrice());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    @DisplayName("Update an existing book. Throws an exception for the wrong book id")
    public void update_WrongBookId_ShouldThrowException() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(book.getTitle());
        requestDto.setIsbn(book.getIsbn());
        requestDto.setDescription(book.getDescription());
        requestDto.setPrice(book.getPrice());

        Mockito.when(bookRepository.existsById(Mockito.anyLong())).thenReturn(false);
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.update(Mockito.anyLong(), requestDto));
    }

    @Test
    @DisplayName("Search a book using search parameters")
    public void search_ValidSearchParameters_ShouldReturnBookDtos() {
        BookSearchParametersDto searchParameters =
                new BookSearchParametersDto(new String[1], new String[1]);

        Specification<Book> bookSpecification = Mockito.mock(Specification.class);

        Mockito.when(bookSpecificationBuilder.build(searchParameters))
                .thenReturn(bookSpecification);

        List<Book> books = List.of(book, book2, book3);
        Mockito.when(bookRepository.findAll(bookSpecification)).thenReturn(books);
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);
        Mockito.when(bookMapper.toDto(book2)).thenReturn(book2Dto);
        Mockito.when(bookMapper.toDto(book3)).thenReturn(book3Dto);

        List<BookDto> expected = List.of(bookDto, book2Dto, book3Dto);
        List<BookDto> actual = bookService.search(searchParameters);
        Assertions.assertTrue(actual.containsAll(expected));
        Assertions.assertEquals(expected.size(), actual.size());
    }

    @Test
    @DisplayName("Find books by category id")
    public void getBooksByCategoryId_ValidCategoryId_ShouldReturListOfBooks() {
        BookDtoWithoutCategoryIds book1Dto = new BookDtoWithoutCategoryIds();
        book1Dto.setTitle(book.getTitle());
        book1Dto.setIsbn(book.getIsbn());
        book1Dto.setId(book.getId());
        book1Dto.setPrice(book.getPrice());
        book1Dto.setDescription(book.getDescription());

        BookDtoWithoutCategoryIds book2Dto = new BookDtoWithoutCategoryIds();
        book2Dto.setTitle(book2.getTitle());
        book2Dto.setIsbn(book2.getIsbn());
        book2Dto.setId(book2.getId());
        book2Dto.setPrice(book2.getPrice());
        book2Dto.setDescription(book2.getDescription());

        BookDtoWithoutCategoryIds book3Dto = new BookDtoWithoutCategoryIds();
        book3Dto.setTitle(book3.getTitle());
        book3Dto.setIsbn(book3.getIsbn());
        book3Dto.setId(book3.getId());
        book3Dto.setPrice(book3.getPrice());
        book3Dto.setDescription(book3.getDescription());

        List<Book> books = List.of(book, book2, book3);
        Long categoryId = 1L;

        Mockito.when(bookRepository.findAllByCategoryId(categoryId)).thenReturn(books);
        Mockito.when(bookMapper.toDtoWithoutCategories(book)).thenReturn(book1Dto);
        Mockito.when(bookMapper.toDtoWithoutCategories(book2)).thenReturn(book2Dto);
        Mockito.when(bookMapper.toDtoWithoutCategories(book3)).thenReturn(book3Dto);

        List<BookDtoWithoutCategoryIds> expected = List.of(book1Dto, book2Dto, book3Dto);
        List<BookDtoWithoutCategoryIds> actual = bookService.getBooksByCategoryId(categoryId);
        Assertions.assertTrue(actual.containsAll(expected));
        Assertions.assertEquals(expected.size(), actual.size());
    }
}
