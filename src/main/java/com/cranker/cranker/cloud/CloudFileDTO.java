package com.cranker.cranker.cloud;

public record CloudFileDTO(
        String fileName,
        String fileUrl,
        String uploadedBy
) {
}
