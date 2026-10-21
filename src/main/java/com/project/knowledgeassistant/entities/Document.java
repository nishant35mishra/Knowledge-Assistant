package com.project.knowledgeassistant.entities;


import com.project.knowledgeassistant.enums.DocumentAcessLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.filefilter.FalseFileFilter;

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

    @Column(nullable = false)
    private int totalChunks = 0;

    @Column(nullable = false)
    private int totalPages = 0;

    @Column(nullable = false)
    private LocalDateTime uploadDate = LocalDateTime.now();

    @JoinColumn (nullable = false , name = "uploadBy")
    @ManyToOne(fetch = FetchType.LAZY )
    private User uploadBy  ;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentAcessLevel acessLevel;


//    @Column(nullable = false)
//    private String processedFileName;
//
//    @Column(nullable = false)
//    private String processedFileType;
//
//    @Column(nullable = false)
//    private String processedFilePath;


    public Document() {
    }

    // getters and setters
}
