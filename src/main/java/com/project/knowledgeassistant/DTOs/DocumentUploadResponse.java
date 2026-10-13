package com.project.knowledgeassistant.DTOs;

import com.project.knowledgeassistant.enums.DocumentAcessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class DocumentUploadResponse {

    private String documentId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private int totalChunks;
    private boolean processed;
    private String message;
    private String uploadBy ;
    List<ExtractedPage> pages ;
    List<DocumentChunkData> chunks ;
    private DocumentAcessLevel  documentAcessLevel;
    private LocalDateTime uploadDate;
    private String processedFileName;


    private String processedFileType;

    private String processedFilePath;

}
