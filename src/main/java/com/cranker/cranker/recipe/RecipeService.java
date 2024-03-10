package com.cranker.cranker.recipe;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public List<RecipeDTO> getAllRecipesByUser(String email) {
        return RecipeMapper.INSTANCE.entityToDTO(recipeRepository.findAllByUserEmail(email));
    }
}
