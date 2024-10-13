package com.cranker.cranker.ingredient;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record IngredientDTO(
        @Schema(example = "1")
        Long id,
        @Schema(example = "Chicken Breasts Oiled")
        String name,
        @Schema(example = "100")
        Double defaultAmount,
        @Schema(example = "mG")
        String amountType
) {
}
