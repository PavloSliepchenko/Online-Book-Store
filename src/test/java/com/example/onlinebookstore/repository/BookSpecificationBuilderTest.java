package com.example.onlinebookstore.repository;

import com.example.onlinebookstore.dto.book.BookSearchParametersDto;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.repository.specifications.IsbnSpecificationProvider;
import com.example.onlinebookstore.repository.specifications.TitleSpecificationProvider;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookSpecificationBuilderTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Build a specification")
    @Sql(scripts = "classpath:database/books/add-three-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/clear-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void build_ValidParameters_ShouldReturCorrectSpecification() {
        IsbnSpecificationProvider isbnSpecificationProvider = new IsbnSpecificationProvider();
        TitleSpecificationProvider titleSpecificationProvider = new TitleSpecificationProvider();
        List<SpecificationProvider<Book>> providers =
                List.of(isbnSpecificationProvider, titleSpecificationProvider);
        BookSpecificationProviderManager bookSpecificationProviderManager =
                new BookSpecificationProviderManager(providers);
        BookSpecificationBuilder builder =
                new BookSpecificationBuilder(bookSpecificationProviderManager);

        Book book1 = bookRepository.findById(1L).get();
        Book book2 = bookRepository.findById(2L).get();
        Book book3 = bookRepository.findById(3L).get();
        BookSearchParametersDto searchParametersDto = new BookSearchParametersDto(
                new String[]{book1.getTitle(), book3.getTitle()},
                null
        );
        Specification<Book> specification = builder.build(searchParametersDto);
        List<Book> expected = List.of(book1, book3);
        List<Book> actual = bookRepository.findAll(specification);
        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertTrue(actual.containsAll(expected));

        searchParametersDto = new BookSearchParametersDto(
                new String[]{book2.getTitle(), book3.getTitle()},
                new String[]{book1.getIsbn()}
        );
        specification = builder.build(searchParametersDto);
        actual = bookRepository.findAll(specification);
        Assertions.assertTrue(actual.isEmpty());

        searchParametersDto = new BookSearchParametersDto(
                null,
                new String[]{book2.getIsbn()}
        );
        specification = builder.build(searchParametersDto);
        expected = List.of(book2);
        actual = bookRepository.findAll(specification);
        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertTrue(actual.containsAll(expected));

        searchParametersDto = new BookSearchParametersDto(
                null,
                null
        );
        specification = builder.build(searchParametersDto);
        expected = List.of(book1, book2, book3);
        actual = bookRepository.findAll(specification);
        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertTrue(actual.containsAll(expected));

        searchParametersDto = new BookSearchParametersDto(
                new String[]{book2.getTitle(), book3.getTitle()},
                new String[]{book3.getIsbn()}
        );
        specification = builder.build(searchParametersDto);
        expected = List.of(book3);
        actual = bookRepository.findAll(specification);
        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertTrue(actual.containsAll(expected));
    }
}
