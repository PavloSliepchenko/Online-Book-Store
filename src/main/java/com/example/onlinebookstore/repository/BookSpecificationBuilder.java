package com.example.onlinebookstore.repository;

import com.example.onlinebookstore.dto.bookdtos.BookSearchParametersDto;
import com.example.onlinebookstore.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private static final String ISBN = "isbn";
    private static final String TITLE = "title";
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Autowired
    public BookSpecificationBuilder(
            SpecificationProviderManager<Book> bookSpecificationProviderManager) {
        this.bookSpecificationProviderManager = bookSpecificationProviderManager;
    }

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParameters) {
        Specification<Book> specification = null;
        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            Specification<Book> titlesSpecification = bookSpecificationProviderManager
                    .getSpecificationProvider(TITLE)
                    .getSpecification(searchParameters.titles());
            specification = Specification.where(titlesSpecification);
        }
        if (searchParameters.isbns() != null && searchParameters.isbns().length > 0) {
            Specification<Book> isbnsSpecification = bookSpecificationProviderManager
                    .getSpecificationProvider(ISBN)
                    .getSpecification(searchParameters.isbns());
            specification = specification == null
                    ? Specification.where(isbnsSpecification)
                    : specification.and(isbnsSpecification);
        }
        return specification == null ? Specification.where(null) : specification;
    }
}
