package com.project.knowledgeassistant.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class SourceResponse {

    String documentId ;
    String fileName ;
    Integer pageNumber ;
    Integer chunkNumber ;
}
