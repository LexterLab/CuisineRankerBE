package com.cranker.cranker.recipe;

import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final Logger logger = LogManager.getLogger(this);

    public List<RecipeDTO> getAllRecipesByUser(String email) {
        logger.info("Retrieving personal recipes for User: {}", email);
        return RecipeMapper.INSTANCE.entityToDTO(recipeRepository.findAllByUserEmail(email));
    }

    @Transactional
    public void deletePersonalRecipe(String email, Long recipeId) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Email", email));

        Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "recipeId, userId", recipeId + ", " +
                        user.getId()));

        recipeRepository.delete(recipe);
        logger.info("Deleted Personal recipe: {},  by User:{}", recipe.getName(), email);
    }
}
