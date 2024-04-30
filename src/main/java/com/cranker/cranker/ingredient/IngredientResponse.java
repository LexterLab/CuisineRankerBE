package com.cranker.cranker.ingredient;

import java.util.List;

public record IngredientResponse(
        int pageNo,
        int pageSize,
        Long totalElements,
        int totalPages,
        boolean last,
        List<IngredientDTO> ingredients
) {
}
