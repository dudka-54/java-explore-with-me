package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        log.debug("Получение категорий: from={}, size={}", from, size);

        Pageable pageable = PageRequest.of(from / size, size);
        Page<Category> catPage = categoryRepository.findAll(pageable);

        log.info("Найдено {} категорий", catPage.getTotalElements());

        return catPage.getContent().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        log.debug("Получение категории по ID: {}", id);
        Category category = findCategoryOrThrow(id);
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto addCategory(NewCategoryDto categoryDto) {
        log.debug("Добавление новой категории: {}", categoryDto);

        if (categoryRepository.existsByName(categoryDto.getName())) {
            log.warn("Попытка создать категорию с существующим name: {}", categoryDto.getName());
            throw new ConflictException("name: " + categoryDto.getName() + " уже существует");
        }
        try {
            Category category = categoryMapper.toCatFromRequest(categoryDto);
            Category newCategory = categoryRepository.save(category);

            log.info("Катерия создана: id={}, name={}", newCategory.getId(), categoryDto.getName());
            return categoryMapper.toDto(newCategory);

        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка целостности данных: {}", e.getMessage());
            throw new ConflictException("Ошибка при создании категории. Возможно, name уже используется.");
        }
    }

    @Override
    public void deleteCategory(Long catId) {
        //ДОБАВИТЬ ПОТОМ ПРОВЕРКУ EVENT
        log.debug("Запрос на удаление категории с id - {}");
        Category category = findCategoryOrThrow(catId);
        try {
            categoryRepository.delete(category);
            log.info("Категория удалена: id={}, name={}", catId, category.getName());
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при удалении категории: {}", e.getMessage());
            throw new ConflictException("Невозможно удалить категорию, так как она связана с другими данными.");
        }
    }

    @Override
    public CategoryDto patchCategory(NewCategoryDto categoryDto, Long id) {
        Category category = findCategoryOrThrow(id);

        if (categoryRepository.existsByNameAndIdNot(categoryDto.getName(), id)) {
            log.warn("Попытка создать категорию с существующим name: {}", categoryDto.getName());
            throw new ConflictException("name: " + categoryDto.getName() + " уже существует");
        }
        try {
            category.setName(categoryDto.getName());
            Category updatedCategory = categoryRepository.save(category);

            log.info("Категория обновлена: id={}, name={}", updatedCategory.getId(), updatedCategory.getName());
            return categoryMapper.toDto(updatedCategory);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при обновлении категории: {}", e.getMessage());
            throw new ConflictException("Невозможно обновить категорию, так как она связана с другими данными.");
        }
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Категория не найдена: id={}", id);
                    return new NotFoundException("Категория с ID " + id + " не найден");
                });
    }
}
