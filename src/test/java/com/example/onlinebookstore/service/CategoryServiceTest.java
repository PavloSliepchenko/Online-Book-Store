package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.category.CategoryResponseDto;
import com.example.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.example.onlinebookstore.exception.EntityNotFoundException;
import com.example.onlinebookstore.mapper.CategoryMapper;
import com.example.onlinebookstore.model.Category;
import com.example.onlinebookstore.repository.CategoryRepository;
import com.example.onlinebookstore.service.impl.CategoryServiceImpl;
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

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    private Category category1;
    private Category category2;
    private Category category3;
    private CategoryResponseDto category1Dto;
    private CategoryResponseDto category2Dto;
    private CategoryResponseDto category3Dto;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    private void initCategories() {
        category1 = new Category();
        category1.setId(1L);
        category1.setName("Education");
        category1.setDescription("Books for education");

        category2 = new Category();
        category2.setId(2L);
        category2.setName("Fiction");
        category2.setDescription("books and stories about imaginary people and events");

        category3 = new Category();
        category3.setId(3L);
        category3.setName("Romance");
        category3.setDescription("Its focus on the relationship between two people");

        category1Dto = new CategoryResponseDto();
        category1Dto.setId(category1.getId());
        category1Dto.setName(category1.getName());
        category1Dto.setDescription(category1.getDescription());

        category2Dto = new CategoryResponseDto();
        category2Dto.setId(category2.getId());
        category2Dto.setName(category2.getName());
        category2Dto.setDescription(category2.getDescription());

        category3Dto = new CategoryResponseDto();
        category3Dto.setId(category3.getId());
        category3Dto.setName(category3.getName());
        category3Dto.setDescription(category3.getDescription());
    }

    @Test
    @DisplayName("Find all categories")
    public void findAll_ValidRequest_ShouldReturnListOfCategoryDtos() {
        List<Category> categories = List.of(category1, category2, category3);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());
        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Mockito.when(categoryMapper.toDto(category1)).thenReturn(category1Dto);
        Mockito.when(categoryMapper.toDto(category2)).thenReturn(category2Dto);
        Mockito.when(categoryMapper.toDto(category3)).thenReturn(category3Dto);

        List<CategoryResponseDto> expected = List.of(category1Dto, category2Dto, category3Dto);
        List<CategoryResponseDto> actual = categoryService.findAll(pageable);

        Assertions.assertTrue(actual.containsAll(expected));
        Assertions.assertEquals(expected.size(), actual.size());
    }

    @Test
    @DisplayName("Get a category by its id")
    public void getById_ValidId_ShouldReturnDtoById() {
        Long categoryId = 2L;
        CategoryResponseDto expected = category2Dto;
        Mockito.when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.ofNullable(category2));
        Mockito.when(categoryMapper.toDto(category2)).thenReturn(expected);
        CategoryResponseDto actual = categoryService.getById(categoryId);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    @DisplayName("Get a category by its id. Throws an exception for the wrong category id")
    public void getById_WrongId_ShouldThrowException() {
        Long categoryId = 1L;
        Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(categoryId));
    }

    @Test
    @DisplayName("Save a new category")
    public void save_ValidSaveRequest_ShouldReturnCategoryDto() {
        CreateCategoryRequestDto categoryRequestDto = new CreateCategoryRequestDto();
        categoryRequestDto.setName(category3.getName());
        categoryRequestDto.setDescription(category3.getDescription());

        CategoryResponseDto expected = category3Dto;
        Mockito.when(categoryMapper.toModel(categoryRequestDto)).thenReturn(category3);
        Mockito.when(categoryRepository.save(category3)).thenReturn(category3);
        Mockito.when(categoryMapper.toDto(category3)).thenReturn(expected);

        CategoryResponseDto actual = categoryService.save(categoryRequestDto);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    @DisplayName("Update an existing category")
    public void update_ValidUpdateRequest_ShouldReturnCategoryDto() {
        CreateCategoryRequestDto categoryRequestDto = new CreateCategoryRequestDto();
        categoryRequestDto.setName(category2.getName());
        categoryRequestDto.setDescription(category2.getDescription());
        Long categoryId = 2L;
        Mockito.when(categoryRepository.existsById(categoryId)).thenReturn(true);
        Mockito.when(categoryMapper.toModel(categoryRequestDto)).thenReturn(category2);
        Mockito.when(categoryRepository.save(category2)).thenReturn(category2);
        Mockito.when(categoryMapper.toDto(category2)).thenReturn(category2Dto);

        CategoryResponseDto expected = category2Dto;
        CategoryResponseDto actual = categoryService.update(categoryId, categoryRequestDto);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    @DisplayName("Update an existing category. Throws an exception for the wrong category id")
    public void update_WrongId_ShouldThrowException() {
        Mockito.when(categoryRepository.existsById(Mockito.anyLong())).thenReturn(false);
        Long categoryId = 1L;
        CreateCategoryRequestDto categoryRequestDto = new CreateCategoryRequestDto();
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(categoryId, categoryRequestDto));
    }
}
