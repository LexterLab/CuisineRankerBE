package com.cranker.cranker.recipe.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RecipeDTO
        (
                @Schema(example = "1")
                Long id,
                @Schema(example = "Chicken Breasts")
                String name,
                @Schema(example = "https://storage.googleapis.com/cuisine-media/recipe.png")
                String pictureURL,
                @Schema(example = "1.Slice up some onions. 2. Slice up some carrots etc\"")
                String preparation,
                @Schema(example = "Personal")
                String type,
                @Schema(example = "1")
                Integer prepTimeInMinutes,
                @Schema(example = "1")
                Integer cookTimeInMinutes,
                @Schema(example = "@")
                Integer totalTime,
                LocalDateTime createdAt,
                LocalDateTime updatedAt

        ) {}
