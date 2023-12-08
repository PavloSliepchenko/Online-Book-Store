package com.example.onlinebookstore.repository;

import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.repository.specifications.IsbnSpecificationProvider;
import com.example.onlinebookstore.repository.specifications.TitleSpecificationProvider;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BookSpecificationProviderManagerTest {
    private IsbnSpecificationProvider isbnSpecificationProvider;
    private TitleSpecificationProvider titleSpecificationProvider;
    private BookSpecificationProviderManager bookSpecificationProviderManager;

    @BeforeEach
    private void setUp() {
        isbnSpecificationProvider = new IsbnSpecificationProvider();
        titleSpecificationProvider = new TitleSpecificationProvider();
        List<SpecificationProvider<Book>> providers =
                List.of(isbnSpecificationProvider, titleSpecificationProvider);
        bookSpecificationProviderManager = new BookSpecificationProviderManager(providers);
    }

    @Test
    @DisplayName("Get a specification provider")
    public void getSpecificationProvider_ValidKey_ShouldReturnProvider() {
        String isbnSpecificationProviderKey = isbnSpecificationProvider.getKey();
        SpecificationProvider<Book> actual =
                bookSpecificationProviderManager
                        .getSpecificationProvider(isbnSpecificationProviderKey);
        Assertions.assertEquals(isbnSpecificationProviderKey, actual.getKey());

        String titleSpecificationProviderKey = titleSpecificationProvider.getKey();
        actual = bookSpecificationProviderManager
                .getSpecificationProvider(titleSpecificationProviderKey);
        Assertions.assertEquals(titleSpecificationProviderKey, actual.getKey());
    }

    @Test
    @DisplayName("Get a specification provider. Throws an exception")
    public void getSpecificationProvider_WrongKey_ShouldThrowException() {
        String key = "Unknown provider";
        Assertions.assertThrows(RuntimeException.class,
                () -> bookSpecificationProviderManager.getSpecificationProvider(key));
    }
}
