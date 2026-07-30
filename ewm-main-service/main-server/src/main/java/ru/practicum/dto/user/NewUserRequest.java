package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "Имя не должно быть пустым")
    @Size(max = 250, min = 2, message = "max = 254, min = 6")
    private String name;

    @NotBlank(message = "email не должно быть пустым")
    @Email
    @Size(max = 254, min = 6, message = "max = 254, min = 6")
    private String email;
}
