package com.cranker.cranker.recipe.payload

import io.swagger.v3.oas.annotations.media.Schema

data class RecipeIngredientInfo(
    val id: Long,
    @Schema(example = "Paprika")
    val name: String,
    @Schema(example = "20")
    val amount: Double,
    @Schema(example = "mG")
    val amountType: String
)
