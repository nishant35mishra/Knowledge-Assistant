package com.project.knowledgeassistant.DTOs;

import lombok.Data;

import java.nio.file.Path;

@Data
public class OriginalDocument {

    private String processedFileName;


    private String processedFileType;

    private Path processedFilePath;
}
