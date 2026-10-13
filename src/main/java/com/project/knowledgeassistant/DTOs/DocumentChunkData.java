package com.project.knowledgeassistant.DTOs;

public record DocumentChunkData(
        String content,
        int pageNumber,
        int chunkNumber
) {
}
