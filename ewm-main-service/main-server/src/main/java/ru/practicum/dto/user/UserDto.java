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
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    @Size(max = 255, message = "Email не может быть длиннее 255 символов")
    private String email;


    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 255, message = "Имя должно быть от 2 до 255 символов")
    private String name;
}