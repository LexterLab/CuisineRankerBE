package com.cranker.cranker.recipe.payload.mapper;

import com.cranker.cranker.recipe.Recipe;
import com.cranker.cranker.recipe.RecipeIngredient;
import com.cranker.cranker.recipe.payload.RecipeDTO;
import com.cranker.cranker.recipe.payload.RecipeIngredientInfo;
import com.cranker.cranker.recipe.payload.RecipeRequestDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RecipeMapper {
    RecipeMapper INSTANCE = Mappers.getMapper(RecipeMapper.class);
    @Mapping(expression = "java(recipe.getPrepTimeInMinutes() + recipe.getCookTimeInMinutes())", target = "totalTime")
    RecipeDTO entityToDTO(Recipe recipe);

    Recipe requestDTOToEntity(RecipeRequestDTO requestDTO);

    @Mapping(expression = "java(recipeIngredient.getIngredient().getId())", target = "id")
    @Mapping(expression = "java(recipeIngredient.getIngredient().getName())", target = "name")
    @Mapping(expression = "java(recipeIngredient.getIngredient().getAmountType())", target = "amountType")
    @Mapping(expression = "java(recipeIngredient.getIngredientAmount())", target = "amount")
    RecipeIngredientInfo recipeIngredientToRecipeIngredientInfo(RecipeIngredient recipeIngredient);


    List<RecipeIngredientInfo> recipeIngredientToRecipeIngredientInfo(Set<RecipeIngredient> recipeIngredient);

    List<RecipeDTO> entityToDTO(Iterable<Recipe> recipes);
}