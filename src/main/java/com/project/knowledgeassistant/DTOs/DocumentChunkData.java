package com.project.knowledgeassistant.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;


public record DocumentChunkData(
        String content,
        int pageNumber,
        int chunkNumber
) {
}
