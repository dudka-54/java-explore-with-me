package ru.practicum;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitDto {
    @NotBlank(message = "App не должен быть null или пустым")
    @Size(max = 255, message = "App имя слишком длинное (максимум 255)")
    private String app;

    @NotBlank(message = "URI не может быть null или пустым")
    @Size(max = 512, message = "URI слишком длинное (максимум 512)")
    private String uri;

    @NotBlank(message = "IP не может быть null или пустым")
    private String ip;

    @NotNull(message = "Timestamp не может быть null")
    @PastOrPresent(message = "Timestamp не может быть в будущем")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
