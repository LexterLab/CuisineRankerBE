package com.cranker.cranker.notification.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record NotificationRequestDTO(
        @Schema(example = "Friendship request")
        @NotBlank(message = "Field title cannot be blank")
        @Size(min = 4, max = 50, message = "Field title must be 4-30 characters")
        String title,
        @Schema(example = "You've received friendship request from Antonio")
        @NotBlank(message = "Field message cannot be blank")
        @Size(message = "Field message must be 4-50 characters", max = 50, min = 4)
        String message
) {
}
