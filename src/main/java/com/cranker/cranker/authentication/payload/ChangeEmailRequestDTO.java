package com.cranker.cranker.authentication.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record ChangeEmailRequestDTO (
        @Schema(example = "user@gmail.com")
        @NotEmpty(message = "Please input your old email")
        @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Please enter a valid old email address.")
        String oldEmail,
        @Schema(example = "user2@gmail.com")
        @NotEmpty(message = "Please input new email")
        @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Please enter a new valid email address.")
        String newEmail
){}
