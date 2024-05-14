package com.cranker.cranker.token.payload;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenDTO(
        @Schema(example = "token")
        String value
) {
}
