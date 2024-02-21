package com.cranker.cranker.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppConstants {
    EMAIL_TOKEN_SPAN("Email Token Span", 900L),
    RESET_TOKEN_SPAN("Reset Token Span", 500L);


    private final String name;
    private final Long value;
}
