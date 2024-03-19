package com.cranker.cranker.authentication.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record TwoFactorRequestDTO(
        @NotEmpty(message = "Please enter the 2fa code you received!")
        @Length(min = 4, max = 4, message = "Please enter a valid token")
        @Schema(example = "Token")
        String token

) {
}
