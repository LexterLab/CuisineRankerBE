package com.cranker.cranker.user.payload;

import java.util.List;

public record UserResponse(
        int pageNo,
        int pageSize,
        Long totalElements,
        int totalPages,
        boolean last,
        List<UserDTO> users
) {
}
