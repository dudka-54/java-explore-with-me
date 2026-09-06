package ru.practicum.dto.comment;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatchCommentDto {
    @Size(min = 5, max = 7000, message = "Текст должен быть от 20 до 7000 символов")
    private String text;
}