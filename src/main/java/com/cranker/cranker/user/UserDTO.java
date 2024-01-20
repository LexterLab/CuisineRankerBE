package com.cranker.cranker.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserDTO(
        Long id,
        @Schema(example = "Jordan Belfort")
        String name,
        @Schema(example = "jordan@gmail.com")
        String email,
        Boolean isVerified
){}
