package com.project.knowledgeassistant.entities;


import com.project.knowledgeassistant.enums.DocumentAcessLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@Table(name = "documents")
public class Document {

    @Id
    private String id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private boolean processed = false;

    @Column(nullable = true)
    private int totalChunks = 0;

    @Column(nullable = true)
    private int totalPages = 0;

    @Column(nullable = false)
    private LocalDateTime uploadDate = LocalDateTime.now();

    @Column(nullable = false)
    private String uploadBy = "System";

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private DocumentAcessLevel acessLevel;


    private String processedFileName;


    private String processedFileType;

    private String processedFilePath;


    public Document() {
    }

    // getters and setters
}
