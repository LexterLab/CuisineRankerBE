package com.cranker.cranker.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record LoginRequestDTO(
        @Schema(example = "user")
        @NotEmpty(message = "Please enter your email address")
        @Email(message = "Please enter a valid email")
        String email,

        @NotEmpty(message = "Please enter your password")
        @Schema(example = "!user123")
        String password
) { }
