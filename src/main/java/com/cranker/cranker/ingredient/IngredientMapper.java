package com.cranker.cranker.ingredient;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface IngredientMapper {
    IngredientMapper INSTANCE = Mappers.getMapper(IngredientMapper.class);

    IngredientDTO ingredientToIngredientDTO(Ingredient ingredient);

    List<IngredientDTO> ingredientsToIngredientDTOs(List<Ingredient> ingredients);

    @Mapping(target = "ingredients", source = "ingredientDTOs")
    @Mapping(target = "pageNo", expression = "java(page.getNumber())")
    @Mapping(target = "pageSize", expression = "java(page.getSize())")
    @Mapping(target = "totalElements", expression = "java(page.getTotalElements())")
    @Mapping(target = "totalPages", expression = "java(page.getTotalPages())")
    @Mapping(target = "last", expression = "java(page.isLast())")
    IngredientResponse pageToIngredientResponse(Page<Ingredient> page, List<IngredientDTO> ingredientDTOs);
}
