package com.cranker.cranker.recipe.payload

import com.cranker.cranker.ingredient.IngredientResponse

data class RecipeInfo(val recipeDetails: RecipeDTO, val ingredients: IngredientResponse)
