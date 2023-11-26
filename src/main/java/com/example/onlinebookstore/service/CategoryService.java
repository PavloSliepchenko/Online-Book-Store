package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.category.CategoryResponseDto;
import com.example.onlinebookstore.dto.category.CreateCategoryRequestDto;
import java.util.List;

public interface CategoryService {
    List<CategoryResponseDto> findAll();

    CategoryResponseDto getById(Long id);

    CategoryResponseDto save(CreateCategoryRequestDto categoryDto);

    CategoryResponseDto update(Long id, CreateCategoryRequestDto categoryDto);

    void deleteById(Long id);
}
