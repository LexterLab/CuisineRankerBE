package com.cranker.cranker.unit.ingredient;

import com.cranker.cranker.ingredient.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IngredientControllerUnitTest {
    @Mock
    private IngredientService ingredientService;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private IngredientController controller;

    @Test
    void shouldRespondWithPagedIngredientsByNameAndOKStatus() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Cooking oil");
        ingredient.setAmountType(AmountType.ML);
        ingredient.setDefaultAmount(500.00);
        ingredient.setId(1L);

        String searchWord = "oil";

        IngredientDTO ingredientDTO = new IngredientDTO(ingredient.getId(), ingredient.getName(),
                ingredient.getDefaultAmount(), ingredient.getAmountType());

        IngredientResponse  ingredientResponse = new IngredientResponse(0, 10, 1L,
                1, true, List.of(ingredientDTO));


        when(ingredientService
                .retrieveIngredientsSearched(searchWord,0, 10, "name", "asc"))
                .thenReturn(ingredientResponse);

        ResponseEntity<IngredientResponse> response = controller
                .searchIngredients(searchWord, 0, 10, "name", "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ingredientResponse, response.getBody());

    }
}
