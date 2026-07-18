package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

public class EventShortDto {
    @NotBlank(message = "Аннотация не может быть пустой")
    @Size(min = 20, max = 2000, message = "Аннотация должна быть от 20 до 2000 символов")
    private String annotation;

    @NotNull(message = "Категория обязательна")
    @Valid
    private CategoryDto category;

    @PositiveOrZero(message = "Количество заявок не может быть отрицательным")
    private Long confirmedRequests;

    @NotNull(message = "Дата события обязательна")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Long id;

    @NotNull(message = "Инициатор обязателен")
    @Valid
    private UserShortDto initiator;

    @NotNull(message = "Поле paid обязательно")
    private Boolean paid;

    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 3, max = 120, message = "Заголовок должен быть от 3 до 120 символов")
    private String title;

    @PositiveOrZero(message = "Количество просмотров не может быть отрицательным")
    private Long views;
}
