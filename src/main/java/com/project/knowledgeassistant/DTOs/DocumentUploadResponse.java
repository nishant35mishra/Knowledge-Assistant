package com.project.knowledgeassistant.DTOs;

import com.project.knowledgeassistant.entities.User;
import com.project.knowledgeassistant.enums.DocumentAcessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@RequiredArgsConstructor
public class DocumentUploadResponse {

    private String documentId;
    private String fileName;
    private boolean processed;
    private String message;


//    private String fileType;
//    private Long fileSize;
//    rivate String processedFileType;
////
////    private String processedFilePath;private int totalChunks;
//    private User uploadBy ;
//    List<ExtractedPage> pages ;
//    List<DocumentChunkData> chunks ;
//    private DocumentAcessLevel  documentAcessLevel;
//    private LocalDateTime uploadDate;
//    private String processedFileName;
//
//
//    p

}
