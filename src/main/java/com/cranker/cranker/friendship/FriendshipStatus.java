package com.cranker.cranker.friendship;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FriendshipStatus {
    PENDING("Pending"),
    ACTIVE("Active"),
    BLOCKED("Blocked");

    private final String name;
}
