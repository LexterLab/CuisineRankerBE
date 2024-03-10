package com.cranker.cranker.ingredient;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AmountType {
    ML("mL"),
    MG("mG");

    private final String name;
}
