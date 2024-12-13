package org.example.doctorai.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestAuthorization {

    @Schema(description = "Логин", example = "user")
    @Size(min = 5, max = 255, message = "Логин должен содержать от 5 до 255 символов")
    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    @Schema(description = "Пароль", example = "password")
    @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}
