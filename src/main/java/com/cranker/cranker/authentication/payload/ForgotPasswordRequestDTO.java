package com.cranker.cranker.authentication.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record ForgotPasswordRequestDTO(
        @Schema(example = "jamesbond@gmail.com")
        @NotEmpty(message = "Please enter an email address.")
        @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Please enter a valid email address.")
        String email
) {}
