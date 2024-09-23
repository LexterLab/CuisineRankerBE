package com.cranker.cranker.notification.payload;

import lombok.Builder;

import java.util.List;

@Builder
public record NotificationResponseDTO(
        int pageNo,
        int pageSize,
        Long totalElements,
        int totalPages,
        boolean last,
        List<NotificationDTO> notifications
) {
}
