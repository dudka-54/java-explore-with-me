package ru.practicum.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.model.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long id);

    @Transactional
    CategoryDto addCategory(NewCategoryDto categoryDto);

    @Transactional
    void deleteCategory(Long catId);

    @Transactional
    CategoryDto patchCategory(NewCategoryDto categoryDto, Long catId);
}
