package com.cranker.cranker.recipe;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public Page<Recipe> getAllRecipesByUser(String email) {
        Map<String, String> filters = new HashMap<>();
        filters.put("_limit", "10");
        filters.put("_offset", "0");
        return recipeRepository.findAllWithPaginationAndSorting(filters, Page.class);
    }
}
