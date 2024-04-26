package com.cranker.cranker.recipe;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.util.Map;

public record RecipeRequestDTO (
        @NotEmpty(message = "Please input recipe name")
        @Size(min = 2, max = 50, message = "Recipe name must be between 2 and 50 characters")
        @Schema(example = "Fried Chicken")
        String name,
        @Schema(example = "1.Slice up some onions. 2. Slice up some carrots etc")
        @NotEmpty(message = "Please input recipe preparation steps")
        String preparation,
        @URL
        @Schema(example = "https://storage.googleapis.com/cuisine-media/defaults/dishes4.png")
        String pictureURL,
        @Min(value = 1, message = "Preparation time must be at least 1 minute")
        Integer prepTimeInMinutes,
        @Min(value = 1, message = "Cooking time must be at least 1 minute")
        Integer cookTimeInMinutes,
        @NotNull(message = "Please input ingredients for the recipe")
        @Schema(example = "{\"1\": 2.5, \"2\": 1.0}")
        Map<Long, Double> ingredients
) {
}
