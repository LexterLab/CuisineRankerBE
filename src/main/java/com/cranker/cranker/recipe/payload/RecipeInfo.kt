package com.cranker.cranker.recipe.payload

data class RecipeInfo(val recipeDetails: RecipeDTO, val ingredients: List<RecipeIngredientInfo>)
