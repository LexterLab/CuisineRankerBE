package com.cranker.cranker.notification.model.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationType {
    FRIENDSHIP_ADD_TOKEN("New friend via friendship token"),
    FRIENDSHIP_ACCEPTED("New friend added"),
    FRIENDSHIP_REQUESTED("New friend request"),;

    private final String TITLE ;
}
