package com.cranker.cranker.unit.recipe;

import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.recipe.*;
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceUnitTest {
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private UserRepository userRepository;

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
        recipe.setPrepTimeInMinutes(1);
        recipe.setCookTimeInMinutes(1);


        RecipeDTO expectedRecipe = new RecipeDTO
                (
                        recipe.getId(), recipe.getName(), recipe.getPictureURL(), recipe.getPreparation(),
                        recipe.getType(), recipe.getPrepTimeInMinutes(), recipe.getCookTimeInMinutes(),
                        2, recipe.getCreatedAt(), recipe.getUpdatedAt()
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

    @Test
    void shouldDeletePersonalRecipeWhenProvidedValidId() {
        String userEmail = "user@example.com";
        Long recipeId = 1L;
        User user = new User();
        user.setId(1L);
        user.setEmail(userEmail);
        Recipe recipe = new Recipe();
        recipe.setUser(user);
        recipe.setId(recipeId);

        when(userRepository.findUserByEmailIgnoreCase(userEmail)).thenReturn(Optional.of(user));
        when(recipeRepository.findByIdAndUserId(recipeId, user.getId())).thenReturn(Optional.of(recipe));
        doNothing().when(recipeRepository).delete(recipe);

        recipeService.deletePersonalRecipe(userEmail, recipeId);

        verify(recipeRepository).delete(any());
    }

    @Test
    void shouldThrowNotFoundWhenDeletingUnExistingRecipe() {
        String userEmail = "user@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(userEmail);
        Long unExistingRecipeId = 1L;

        when(userRepository.findUserByEmailIgnoreCase(userEmail)).thenReturn(Optional.of(user));
        when(recipeRepository.findByIdAndUserId(unExistingRecipeId, user.getId()))
                .thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> recipeService
                .deletePersonalRecipe(userEmail, unExistingRecipeId));
    }

}
