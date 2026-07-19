package ru.practicum.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatClient;
import ru.practicum.controller.StatsMainSaver;
import ru.practicum.dto.category.CategoryDto;
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
    public ResponseEntity<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     HttpServletRequest request) {
        saveStat(uri, request);
        return null;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategory(@PathVariable("catId") Long catId,
                                                         HttpServletRequest request) {
        saveStat(uri, request, String.valueOf(catId));
        return null;
    }

    @Override
    public StatClient getStatsClient() {
        return statClient;
    }
}
