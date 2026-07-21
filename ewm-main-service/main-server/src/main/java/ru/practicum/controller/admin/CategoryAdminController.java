package ru.practicum.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatClient;
import ru.practicum.controller.StatsMainSaver;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController implements StatsMainSaver {
    private final CategoryService categoryService;
    private final StatClient statClient;
    private final String uri = "/admin/categories";

    @PostMapping
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody NewCategoryDto categoryDto,
                                                   HttpServletRequest request) {
        saveStat(uri, request);
        return null;
    }

    @DeleteMapping("/catId")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long catId,
                                               HttpServletRequest request) {
        saveStat(uri, request, String.valueOf(catId));
        return null;
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> patchCategory(@Valid @RequestBody NewCategoryDto categoryDto,
                                                     @PathVariable Long catId,
                                                     HttpServletRequest request) {
        saveStat(uri, request);
        return null;
    }

    @Override
    public StatClient getStatsClient() {
        return statClient;
    }
}
