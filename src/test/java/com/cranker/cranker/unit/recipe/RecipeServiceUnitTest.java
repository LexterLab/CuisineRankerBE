package com.cranker.cranker.unit.recipe;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.ingredient.Ingredient;
import com.cranker.cranker.ingredient.IngredientRepository;
import com.cranker.cranker.recipe.*;
import com.cranker.cranker.user.model.User;
import com.cranker.cranker.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceUnitTest {
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private IngredientRepository ingredientRepository;
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

    @Test
    void shouldCreatePersonalRecipe() {
        String userEmail = "user@example.com";
        Long ingredientId = 1L;
        double amount = 1.0;
        User user = new User();
        user.setId(1L);
        user.setEmail(userEmail);
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);

        RecipeRequestDTO requestDTO = new RecipeRequestDTO
                (
                        "Fried Chicken", "Preparation", "url",
                        1, 1, Map.of(ingredientId, amount)
                );

        Recipe newRecipe = new Recipe();
        newRecipe.setUser(user);
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

        when(userRepository.findUserByEmailIgnoreCase(userEmail)).thenReturn(Optional.of(user));
        when(recipeRepository.existsByNameAndTypeAndUserId(requestDTO.name(), RecipeType.CUSTOM.getName(), user.getId()))
                .thenReturn(false);
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(ingredient));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(newRecipe);

        RecipeDTO createdRecipe = recipeService.createPersonalRecipe(userEmail, requestDTO);

        assertEquals(recipeDTO, createdRecipe);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCreatingPersonalRecipeWithUnexistingUser() {
        String userEmail = "unexisting@example.com";
        User user = new User();
        Long ingredientId = 1L;
        double amount = 1.0;

        RecipeRequestDTO requestDTO = new RecipeRequestDTO
                (
                        "Fried Chicken", "Preparation", "url",
                        1, 1, Map.of(ingredientId, amount)
                );



        when(userRepository.findUserByEmailIgnoreCase(userEmail)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> recipeService
                .createPersonalRecipe(userEmail, requestDTO));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCreatingPersonalRecipeWithUnexistingIngredient() {
        String userEmail = "user@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(userEmail);
        Long ingredientId = 1L;
        double amount = 1.0;

        RecipeRequestDTO requestDTO = new RecipeRequestDTO
                (
                        "Fried Chicken", "Preparation", "url",
                        1, 1, Map.of(ingredientId, amount)
                );

        Recipe newRecipe = new Recipe();
        newRecipe.setUser(user);
        newRecipe.setId(1L);
        newRecipe.setType(RecipeType.CUSTOM);
        newRecipe.setName(requestDTO.name());
        newRecipe.setPreparation(requestDTO.preparation());
        newRecipe.setCookTimeInMinutes(requestDTO.cookTimeInMinutes());
        newRecipe.setPrepTimeInMinutes(requestDTO.prepTimeInMinutes());
        newRecipe.setTotalTimeInMinutes(2);
        newRecipe.setPictureURL(requestDTO.pictureURL());

        when(userRepository.findUserByEmailIgnoreCase(userEmail)).thenReturn(Optional.of(user));
        when(recipeRepository.existsByNameAndTypeAndUserId(requestDTO.name(), RecipeType.CUSTOM.getName(), user.getId()))
                .thenReturn(false);
        when(ingredientRepository.findById(ingredientId)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> recipeService.createPersonalRecipe(userEmail, requestDTO));
    }

    @Test
    void shouldThrowAPIExceptionWhenProvidedExistingPersonalRecipe() {
        String userEmail = "user@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(userEmail);
        Long ingredientId = 1L;
        double amount = 1.0;

        RecipeRequestDTO requestDTO = new RecipeRequestDTO
                (
                        "Fried Chicken", "Preparation", "url",
                        1, 1, Map.of(ingredientId, amount)
                );

        when(userRepository.findUserByEmailIgnoreCase(userEmail)).thenReturn(Optional.of(user));
        when(recipeRepository.existsByNameAndTypeAndUserId(requestDTO.name(), RecipeType.CUSTOM.getName(), user.getId()))
                .thenReturn(true);

        assertThrows(APIException.class, () -> recipeService.createPersonalRecipe(userEmail, requestDTO));
    }

    @Test
    void shouldThrowAPIExceptionWhenProvidedInvalidIngredientAmount() {
        String userEmail = "user@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(userEmail);
        Long ingredientId = 1L;
        double amount = -1;

        RecipeRequestDTO requestDTO = new RecipeRequestDTO
                (
                        "Fried Chicken", "Preparation", "url",
                        1, 1, Map.of(ingredientId, amount)
                );
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);

        when(userRepository.findUserByEmailIgnoreCase(userEmail)).thenReturn(Optional.of(user));
        when(recipeRepository.existsByNameAndTypeAndUserId(requestDTO.name(), RecipeType.CUSTOM.getName(), user.getId()))
                .thenReturn(false);
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(ingredient));

        assertThrows(APIException.class, () -> recipeService.createPersonalRecipe(userEmail, requestDTO));
    }
}

