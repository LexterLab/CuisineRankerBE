package com.cranker.cranker.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppConstants {
    EMAIL_TOKEN_SPAN("Email Token Span", 900L),
    RESET_TOKEN_SPAN("Reset Token Span", 500L),
    CHANGE_EMAIL_TOKEN_SPAN("Change Email Token Span", 500L),
    TWO_FACTOR_TOKEN_SPAN("Two-Factor Token Span", 300L),
    FRIENDSHIP_TOKEN_SPAN("Friendship Token Span", 1200L);


    private final String name;
    private final Long value;
}
