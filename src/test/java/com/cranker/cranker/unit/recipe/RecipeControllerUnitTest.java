package com.cranker.cranker.unit.recipe;

import com.cranker.cranker.recipe.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeControllerUnitTest {
    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeController recipeController;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authentication = mock(Authentication.class);
    }


    @Test
    void shouldRespondWithOKAndPersonalRecipesWhenGivenValidAuthentication() {
        String pictureURL = "URL";
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("recipe-name");
        recipe.setPreparation("preparation");
        recipe.setType(RecipeType.CUSTOM);
        recipe.setPictureURL(pictureURL);

        String userEmail = "user@example.com";

        RecipeDTO expectedRecipe = new RecipeDTO
                (
                        recipe.getId(), recipe.getName(), recipe.getPictureURL(), recipe.getPreparation(),
                        recipe.getType(), recipe.getPrepTimeInMinutes(), recipe.getCookTimeInMinutes(),
                        recipe.getTotalTimeInMinutes(), recipe.getCreatedAt(), recipe.getUpdatedAt()
                );
        List<RecipeDTO> expectedRecipes = List.of(expectedRecipe);

        when(authentication.getName()).thenReturn(userEmail);
        when(recipeService.getAllRecipesByUser(userEmail)).thenReturn(expectedRecipes);

        ResponseEntity<List<RecipeDTO>> response = recipeController.retrieveUserPersonalRecipes(authentication);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedRecipes, response.getBody());
    }

    @Test
    void shouldRespondWithOKAndEmptyRecipeListWhenUserHasNoPersonalRecipes() {
        List<RecipeDTO> expectedEmptyList = new ArrayList<>();

        String userEmail = "user@example.com";

        when(authentication.getName()).thenReturn(userEmail);
        when(recipeService.getAllRecipesByUser(userEmail)).thenReturn(expectedEmptyList);

        ResponseEntity<List<RecipeDTO>> response = recipeController.retrieveUserPersonalRecipes(authentication);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedEmptyList, response.getBody());
        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test
    void shouldRespondWithNoContentWhenDeletingPersonalRecipe() {
        String userEmail = "user@example.com";
        Long recipeId = 1L;

        when(authentication.getName()).thenReturn(userEmail);
        doNothing().when(recipeService).deletePersonalRecipe(userEmail, recipeId);

        ResponseEntity<Void> response = recipeController.deleteUserPersonalRecipe(recipeId, authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void shouldRespondWithCreatedAndRecipe() {
        String userEmail = "user@example.com";
        long ingredientId = 1L;
        double amount = 1.0;
        RecipeRequestDTO requestDTO = new RecipeRequestDTO
                (
                        "Fried Chicken", "Preparation", "url",
                        1, 1, Map.of(ingredientId, amount)
                );

        Recipe newRecipe = new Recipe();
        newRecipe.setId(1L);
        newRecipe.setType(RecipeType.CUSTOM);
        newRecipe.setName(requestDTO.name());
        newRecipe.setPreparation(requestDTO.preparation());
        newRecipe.setCookTimeInMinutes(requestDTO.cookTimeInMinutes());
        newRecipe.setPrepTimeInMinutes(requestDTO.prepTimeInMinutes());
        newRecipe.setTotalTimeInMinutes(2);
        newRecipe.setPictureURL(requestDTO.pictureURL());

        RecipeDTO recipeDTO = new RecipeDTO
                (
                        newRecipe.getId(), newRecipe.getName(), newRecipe.getPictureURL(), newRecipe.getPreparation(),
                        newRecipe.getType(), newRecipe.getPrepTimeInMinutes(), newRecipe.getCookTimeInMinutes(),
                        newRecipe.getTotalTimeInMinutes(), newRecipe.getCreatedAt(), newRecipe.getUpdatedAt()
                );

        when(authentication.getName()).thenReturn(userEmail);
        when(recipeService.createPersonalRecipe(userEmail, requestDTO)).thenReturn(recipeDTO);

        ResponseEntity<RecipeDTO> response = recipeController.createPersonalRecipe(authentication, requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(recipeDTO, response.getBody());
    }

}
