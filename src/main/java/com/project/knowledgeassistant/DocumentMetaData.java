package com.project.knowledgeassistant;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;

@Entity
@Table(name = "document_metadata")

@Data
@Getter
public class DocumentMetaData {

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
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccessLevel accessLevel;
	
	@Column(nullable = false)
	private boolean processed = false;
	
	@Column(nullable = false)
	private int totalChunks = 0;
	
	@Column(nullable = false)
	private LocalDateTime uploadDate = LocalDateTime.now();
	
	@Column(nullable = false)
	private String uploadBy = "System";
	
}