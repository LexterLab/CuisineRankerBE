package com.cranker.cranker.friendship;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record FriendshipDTO(
     @Schema(example = "1")
     Long id,
     @Schema(example = "1")
     Long friendId,
     @Schema(example = "user user")
     String friendName,
     @Schema(example = "example.com")
     String friendImage,
     @Schema(example = "Active")
     String status,
     @Schema(example = "20 April 2024", description = "date of friendship formatted")
     String updatedAtFormatted,
     @Schema(description = "date relationship started")
     LocalDateTime createdAt,
     @Schema(description = "date of friendship")
     LocalDateTime updatedAt

) {
}