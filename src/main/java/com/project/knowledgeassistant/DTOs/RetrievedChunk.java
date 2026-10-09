package com.project.knowledgeassistant.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RetrievedChunk {

    private String content;
    private String documentId;
    private String fileName;
    private Integer pageNumber;
    private Integer chunkNumber;
    private Double score ;
}
