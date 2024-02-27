package com.cranker.cranker.unit.recipe;

import com.cranker.cranker.recipe.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceUnitTest {
    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    void shouldRetrieveAllRecipesMadeByUser() {
        String pictureURL = "URL";
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("recipe-name");
        recipe.setPreparation("preparation");
        recipe.setType(RecipeType.CUSTOM);
        recipe.setPictureURL(pictureURL);


        RecipeDTO expectedRecipe = new RecipeDTO
                (
                        recipe.getId(), recipe.getName(), recipe.getPictureURL(), recipe.getPreparation(),
                        recipe.getType(), recipe.getPrepTimeInMinutes(), recipe.getCookTimeInMinutes(),
                        recipe.getTotalTimeInMinutes(), recipe.getCreatedAt(), recipe.getUpdatedAt()
                );

        String userEmail = "user@example.com";
        List<RecipeDTO> expectedRecipes = List.of(expectedRecipe);

        when(recipeRepository.findAllByUserEmail(userEmail)).thenReturn(List.of(recipe));

        List<RecipeDTO> receivedRecipes = recipeService.getAllRecipesByUser(userEmail);

        assertEquals(expectedRecipes, receivedRecipes);
    }

    @Test
    void shouldReturnEmptyListOfRecipesMadeByUserWhenUserHasNone() {
        List<RecipeDTO> expectedEmptyList = new ArrayList<>();
        List<Recipe> emptyList = new ArrayList<>();

        String userEmail = "user@example.com";

        when(recipeRepository.findAllByUserEmail(userEmail)).thenReturn(emptyList);

        List<RecipeDTO> receivedRecipes = recipeService.getAllRecipesByUser(userEmail);

        assertEquals(expectedEmptyList, receivedRecipes);
        assertTrue(emptyList.isEmpty());
    }

}
