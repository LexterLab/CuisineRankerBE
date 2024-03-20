package com.cranker.cranker.user.payload;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserDTO(
        Long id,
        @Schema(example = "Jordan Belfort")
        String name,
        @Schema(example = "jordan@gmail.com")
        String email,
        @Schema(example = "URL of picture")
        String profilePicURL,
        Boolean isVerified,
        Boolean isTwoFactorEnabled
){}
