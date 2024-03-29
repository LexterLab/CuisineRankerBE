package com.cranker.cranker.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TokenType {
    EMAIL_CONFIRMATION("Email Confirmation Token"),
    RESET_PASSWORD("Reset Password Token");

    private final String name;
}
