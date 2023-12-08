package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.dto.category.CategoryResponseDto;
import com.example.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.example.onlinebookstore.exception.EntityNotFoundException;
import com.example.onlinebookstore.mapper.CategoryMapper;
import com.example.onlinebookstore.model.Category;
import com.example.onlinebookstore.repository.CategoryRepository;
import com.example.onlinebookstore.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponseDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryResponseDto getById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find a category by id " + id));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryResponseDto save(CreateCategoryRequestDto categoryDto) {
        Category category = categoryRepository.save(categoryMapper.toModel(categoryDto));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryResponseDto update(Long id, CreateCategoryRequestDto categoryDto) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("No category was found by id " + id);
        }
        Category category = categoryMapper.toModel(categoryDto);
        category.setId(id);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
