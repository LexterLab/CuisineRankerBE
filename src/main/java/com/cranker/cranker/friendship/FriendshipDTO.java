package com.cranker.cranker.friendship;

import java.time.LocalDateTime;

public record FriendshipDTO(
     Long id,
     Long friendId,
     String friendName,
     String friendImage,
     String status,
     String updatedAtFormatted,
     LocalDateTime createdAt,
     LocalDateTime updatedAt

) {
}