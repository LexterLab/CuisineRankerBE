package com.cranker.cranker.profile_pic.payload;

public record PicturesDTO(
        Long id,
        String name,
        String url,
        String categoryName
) {
}
