package com.cranker.cranker.user.model.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AvailableProvider {
    GOOGLE("Google");

    private final String name;
}
