package ru.practicum.controller.open;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        List<CompilationDto> compilations = compilationService.getCompilations(pinned, from, size);
        return compilations.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(compilations);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilationById(@PathVariable Long compId) {
        CompilationDto compilation = compilationService.getCompilationById(compId);
        return ResponseEntity.ok(compilation);
    }
}