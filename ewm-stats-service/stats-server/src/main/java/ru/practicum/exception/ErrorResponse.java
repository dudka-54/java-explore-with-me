package ru.practicum.exception;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String error;
    private String description;
}
