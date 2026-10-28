package com.project.knowledgeassistant.entities;


import java.time.LocalDateTime;

import com.project.knowledgeassistant.enums.DocumentAcessLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

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
    private LocalDateTime uploadDate = LocalDateTime.now();

    @JoinColumn (nullable = false , name = "uploadBy")
    @ManyToOne(fetch = FetchType.LAZY )
    private User uploadBy  ;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentAcessLevel acessLevel;



    public Document() {
    }

    // getters and setters
}
