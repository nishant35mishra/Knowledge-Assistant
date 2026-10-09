package com.project.knowledgeassistant;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Dto {
	
	private String id;                 
    private String fileName;
    private String fileType;
    private Long fileSize;
    private AccessLevel accessLevel;
    private boolean processed;         
    private int totalChunks;
    private LocalDateTime uploadDate;
    private String uploadBy;
}
