package com.cranker.cranker.ingredient;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientResponse retrieveIngredientsSearched(String name, int pageNo, int pageSize, String sortBy,
                                                            String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Ingredient> ingredients = ingredientRepository.findAllByNameContainingIgnoreCase(name, pageable);

        List<IngredientDTO> ingredientDTOList = IngredientMapper.INSTANCE
                .ingredientsToIngredientDTOs(ingredients.getContent());

        return IngredientMapper.INSTANCE.pageToIngredientResponse(ingredients, ingredientDTOList);
    }
}
