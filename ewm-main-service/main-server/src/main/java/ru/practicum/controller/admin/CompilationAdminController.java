package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;
import ru.practicum.service.CompilationService;


@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> addCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        CompilationDto created = compilationService.addCompilation(newCompilationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(
            @PathVariable Long compId,
            @Valid @RequestBody UpdateCompilationDto updateCompilationDto) {
        CompilationDto updated = compilationService.updateCompilation(compId, updateCompilationDto);
        return ResponseEntity.ok(updated);
    }
}