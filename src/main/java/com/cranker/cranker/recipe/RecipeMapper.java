package com.cranker.cranker.recipe;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RecipeMapper {
    RecipeMapper INSTANCE = Mappers.getMapper(RecipeMapper.class);
    @Mapping(expression = "java(recipe.getPrepTimeInMinutes() + recipe.getCookTimeInMinutes())", target = "totalTime")
    RecipeDTO entityToDTO(Recipe recipe);

    List<RecipeDTO> entityToDTO(Iterable<Recipe> recipes);
}