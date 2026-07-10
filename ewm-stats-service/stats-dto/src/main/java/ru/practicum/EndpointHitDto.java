package ru.practicum;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class EndpointHitDto {
    @NotBlank(message = "Поле app не должно быть пустым")

    private String app;

    @NotBlank(message = "Поле app не должно быть пустым")
    private String uri;

    @NotBlank(message = "Поле app не должно быть пустым")
    private String ip;

    @NotNull(message = "timestamp не должен быть null")
    private LocalDateTime timestamp;
}
