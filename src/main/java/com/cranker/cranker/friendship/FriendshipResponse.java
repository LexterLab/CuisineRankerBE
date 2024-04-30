package com.cranker.cranker.friendship;

import java.util.List;

public record FriendshipResponse(
        int pageNo,
        int pageSize,
        Long totalElements,
        int totalPages,
        boolean last,
        List<FriendshipDTO> friendships
) {
}
