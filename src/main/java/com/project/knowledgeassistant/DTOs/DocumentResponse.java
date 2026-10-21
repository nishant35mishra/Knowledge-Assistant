package com.project.knowledgeassistant.DTOs;

import java.time.LocalDateTime;

import com.project.knowledgeassistant.enums.DocumentAcessLevel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    private String documentId;
    private String fileName;
    private String filePath ;
    private String fileType;
    private boolean processed;
    private Long fileSize;
    private int totalChunks;
    private DocumentAcessLevel documentAcessLevel;
    private LocalDateTime uploadDate;
    private RegisterResponseDto registerResponseDto ;



    //    private int totalPages
    //    private String processedFileName;
//    private String processedFileType;
//    private String processedFilePath;



}
