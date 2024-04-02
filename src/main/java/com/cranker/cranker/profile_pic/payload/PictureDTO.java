package com.cranker.cranker.profile_pic.payload;

public record PictureDTO(
        Long id,
        String name,
        String url,
        String categoryName
) {
}
