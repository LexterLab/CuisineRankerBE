package com.cranker.cranker.notification.payload;

import java.util.List;

public record NotificationResponseDTO(
        int pageNo,
        int pageSize,
        Long totalElements,
        int totalPages,
        boolean last,
        List<NotificationDTO> notifications
) {
}
