package com.cranker.cranker.recipe;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.ingredient.Ingredient;
import com.cranker.cranker.ingredient.IngredientRepository;
import com.cranker.cranker.user.model.User;
import com.cranker.cranker.user.repository.UserRepository;
import com.cranker.cranker.utils.Messages;
import com.cranker.cranker.utils.Properties;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final Properties properties;
    private final IngredientRepository ingredientRepository;
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

    @Transactional
    public RecipeDTO createPersonalRecipe(String email, RecipeRequestDTO requestDTO) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Email", email));

        if (recipeRepository.existsByNameAndTypeAndUserId(requestDTO.name(), RecipeType.CUSTOM.getName(), user.getId())) {
            logger.error("Recipe with name {} already exists", requestDTO.name());
            throw new APIException(HttpStatus.CONFLICT, Messages.RECIPE_EXISTS);
        }

        Recipe recipe = RecipeMapper.INSTANCE.requestDTOToEntity(requestDTO);
        recipe.setUser(user);
        recipe.setType(RecipeType.CUSTOM);

        if (requestDTO.pictureURL() == null) {
            recipe.setPictureURL(properties.getDefaultRecipePicture());
        }

        for (var entry: requestDTO.ingredients().entrySet()) {
            Long id = entry.getKey();
            Ingredient ingredient = ingredientRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "Id", id));

            if (entry.getValue() <= 0) {
                logger.error("Ingredient {}'s amount is less than 0", id);
                throw new APIException(HttpStatus.BAD_REQUEST, Messages.INVALID_INGREDIENT_AMOUNT);
            }

            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setRecipe(recipe);
            recipeIngredient.setIngredientAmount(entry.getValue());

            recipe.getRecipeIngredients().add(recipeIngredient);
        }

        logger.info("Created personal recipe: {}, by User:{}", recipe.getName(), email);
        return RecipeMapper.INSTANCE.entityToDTO(recipeRepository.save(recipe));
    }
}
