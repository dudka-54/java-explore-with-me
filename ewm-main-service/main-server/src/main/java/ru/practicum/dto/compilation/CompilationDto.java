package ru.practicum.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompilationDto {
    @NotNull(message = "id не должен быть пуст")
    private Long id;
    private List<EventShortDto> events;

    @NotNull(message = "pinned не должен быть пуст")
    private Boolean pinned;

    @NotBlank(message = "title не должен быть пуст")
    private String title;
}
