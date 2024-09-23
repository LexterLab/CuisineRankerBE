package com.cranker.cranker.notification.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record NotificationDTO(
        Long id,
        @Schema(example = "Friendship request")
        String title,
        @Schema(example = "You've received friendship request from Antonio")
        String message,
        LocalDateTime issued,
        @Schema(example = "2")
        Long minutesSince,
        @Schema(example = "2")
        Long hoursSince,
        @Schema(example = "2")
        Long daysSince,
        @Schema(example = "2")
        Long weeksSince,
        @Schema(example = "2")
        Long monthsSince,
        @Schema(example = "2")
        Long yearsSince
) {
}
