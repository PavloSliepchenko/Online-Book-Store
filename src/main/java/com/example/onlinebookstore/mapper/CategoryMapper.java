package com.example.onlinebookstore.mapper;

import com.example.onlinebookstore.config.MapperConfig;
import com.example.onlinebookstore.dto.category.CategoryResponseDto;
import com.example.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.example.onlinebookstore.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    Category toModel(CreateCategoryRequestDto requestDto);

    CategoryResponseDto toDto(Category category);
}
