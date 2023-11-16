package com.example.onlinebookstore.repository;

import com.example.onlinebookstore.dto.bookdtos.BookSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookSearchParametersDto searchParameters);
}
