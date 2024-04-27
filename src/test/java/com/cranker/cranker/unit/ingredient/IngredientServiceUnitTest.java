package com.cranker.cranker.unit.ingredient;

import com.cranker.cranker.ingredient.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IngredientServiceUnitTest {
    @InjectMocks
    private IngredientService ingredientService;
    @Mock
    private IngredientRepository ingredientRepository;

    @Test
    void shouldRetrieveAllIngredientsPageable() {
        String sortBy = "name";
        int pageNo = 0;
        int pageSize = 10;

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

        Sort sort = Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Ingredient> ingredientPage = new PageImpl<>(List.of(ingredient), pageable, 1);

        when(ingredientRepository.findAllByNameContainingIgnoreCase(searchWord, pageable)).thenReturn(ingredientPage);

        IngredientResponse response = ingredientService
                .retrieveIngredientsSearched(searchWord, 0, 10, sortBy, "asc");

        assertEquals(ingredientResponse, response);
    }
}
