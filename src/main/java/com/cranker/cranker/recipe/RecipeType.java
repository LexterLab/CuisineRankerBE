package com.cranker.cranker.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RecipeType {
    CUSTOM("Personal");

    private final String name;
}
