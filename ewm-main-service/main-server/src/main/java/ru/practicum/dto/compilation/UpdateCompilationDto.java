package ru.practicum.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationDto {
    private List<Long> events;

    private Boolean pinned;

    @Size(max = 50, message = "Название подборки не может быть длиннее 50 символов")
    private String title;
}