package com.cranker.cranker.recipe;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final Logger logger = LogManager.getLogger(this);

    public List<RecipeDTO> getAllRecipesByUser(String email) {
        logger.info("Retrieving personal recipes for User: {}", email);
        return RecipeMapper.INSTANCE.entityToDTO(recipeRepository.findAllByUserEmail(email));
    }
}
