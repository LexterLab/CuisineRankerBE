package com.cranker.cranker.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RecipeType {
    CUSTOM("Custom"),
    SAVED("Saved");

    private final String name;
}
