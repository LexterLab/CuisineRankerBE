package com.cranker.cranker.notification.payload;

import lombok.Builder;

@Builder
public record NotificationRequestDTO(
        String title,
        String message
) {
}
