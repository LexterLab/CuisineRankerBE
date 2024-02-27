package com.cranker.cranker.recipe;

import java.time.LocalDateTime;

public record RecipeDTO
        (
                Long id,
                String name,
                String pictureURL,
                String preparation,
                String type,
                Integer prepTimeInMinutes,
                Integer cookTimeInMinutes,
                Integer totalTime,
                LocalDateTime createdAt,
                LocalDateTime updatedAt

        ) {}
