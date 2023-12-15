package com.example.onlinebookstore.mapper;

import com.example.onlinebookstore.config.MapperConfig;
import com.example.onlinebookstore.dto.category.CategoryResponseDto;
import com.example.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.example.onlinebookstore.model.Category;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    Category toModel(CreateCategoryRequestDto requestDto);

    CategoryResponseDto toDto(Category category);

    @Named("categoryById")
    default Set<Category> categoryById(List<Long> categoryIds) {
        if (categoryIds == null) {
            return null;
        }
        Set<Category> categories = new HashSet<>();
        for (Long id: categoryIds) {
            Category category = new Category();
            category.setId(id);
            categories.add(category);
        }
        return categories;
    }
}
